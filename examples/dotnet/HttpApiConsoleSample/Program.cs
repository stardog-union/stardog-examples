using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.IO.Compression;
using System.Net;
using System.Text;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace HttpApiConsoleSample
{
    // helper class for deserializing the response of the
    // list databases request
    public class DatabaseList
    {
        [JsonProperty("databases")]
        public List<string> Databases { get; set; }
    }

    // helper class for deserializing the response of the
    // create database request
    public class CreateResponse
    {
        [JsonProperty("message")]
        public string Message { get; set; }
    }

    class Program
    {
        static void Main(string[] args)
        {
            const string SERVER_URL = "http://localhost:5820/";
            const string STARDOG_USERNAME = "admin";
            const string STARDOG_PASSWORD = "admin";
            const string DATABASE_NAME = "MyDB";

            Uri baseServerUri = new Uri(SERVER_URL);
            CredentialCache requestCredentialsCache = new CredentialCache();
            requestCredentialsCache.Add(baseServerUri, "Basic", new NetworkCredential(STARDOG_USERNAME, STARDOG_PASSWORD));

            // Get a list of all the server's databases

            HttpWebRequest listDatabasesRequest = (HttpWebRequest)WebRequest.Create(new Uri(baseServerUri, "admin/databases"));

            listDatabasesRequest.ContentType = "application/json";
            listDatabasesRequest.Credentials = requestCredentialsCache;
            listDatabasesRequest.Method = "GET";

            // using will call Dispose() for us
            using (HttpWebResponse listDatabasesResponse = (HttpWebResponse)listDatabasesRequest.GetResponse())
            {
                if (listDatabasesResponse.StatusCode == HttpStatusCode.OK)
                {
                    using (var sr = new StreamReader(listDatabasesResponse.GetResponseStream()))
                    {
                        var databasesAsJson = sr.ReadToEnd();
                        var databases = JsonConvert.DeserializeObject<DatabaseList>(databasesAsJson);
                        if (databases.Databases.Contains(DATABASE_NAME))
                        {
                            // if a database with the same name exists, DROP it

                            HttpWebRequest deleteDatabaseRequest = (HttpWebRequest)WebRequest.Create(new Uri(baseServerUri, $"admin/databases/{DATABASE_NAME}"));
                            deleteDatabaseRequest.Method = "DELETE";
                            deleteDatabaseRequest.Credentials = requestCredentialsCache;
                            using (HttpWebResponse deleteDatabaseResponse = (HttpWebResponse)deleteDatabaseRequest.GetResponse())
                            {
                                if (deleteDatabaseResponse.StatusCode == HttpStatusCode.OK)
                                {
                                    Console.WriteLine($"Database {DATABASE_NAME} deleted!");
                                }
                                else
                                {
                                    Console.WriteLine($"ERROR! {deleteDatabaseResponse.StatusDescription}");
                                }
                            }
                        }

                    }
                }
            }

            // Create the database and add a triple from a file to it.

            HttpWebRequest createDatabasesRequest = (HttpWebRequest)WebRequest.Create(new Uri(baseServerUri, "admin/databases"));

            createDatabasesRequest.Credentials = requestCredentialsCache;
            createDatabasesRequest.Method = "POST";
            createDatabasesRequest.Accept = "*/*";
            createDatabasesRequest.Headers.Add("Cache-Control", "no-cache");
            createDatabasesRequest.Headers.Add("Accept-Encoding", "gzip, deflate, br");
            createDatabasesRequest.UserAgent = "Stardog DotNet Sample";

            // Hand coding multipart requests are tricky. For this section I leaned heavily on the
            // implementation found in dotNetRDF - see https://github.com/dotnetrdf/dotnetrdf/blob/master/Libraries/dotNetRDF/Storage/Management/StardogServer.cs

            string boundary = "--------------------------" + DateTime.Now.Ticks.ToString("x", NumberFormatInfo.InvariantInfo); // just need something unique

            byte[] boundaryBytes = Encoding.ASCII.GetBytes("\r\n--" + boundary + "\r\n");
            byte[] terminatorBytes = Encoding.ASCII.GetBytes("\r\n--" + boundary + "--\r\n");
            createDatabasesRequest.ContentType = "multipart/form-data;; boundary=" + boundary;

            JObject json = new JObject();
            json.Add("dbname", new JValue(DATABASE_NAME));
            json.Add("options", new JObject());

            JObject file = new JObject();
            file.Add("filename", new JValue("fileX.ttl"));

            json.Add("files", new JArray() { file });

            using (Stream s = createDatabasesRequest.GetRequestStream())
            {
                // Boundary
                s.Write(boundaryBytes, 0, boundaryBytes.Length);

                // Root Item
                string rootItem = string.Format("Content-Disposition: form-data; name=\"{0}\"\r\n\r\n{1}", "root", json.ToString());
                byte[] itemBytes = Encoding.UTF8.GetBytes(rootItem);
                s.Write(itemBytes, 0, itemBytes.Length);

                // Boundary
                s.Write(boundaryBytes, 0, boundaryBytes.Length);

                // File Item
                string fileItem = string.Format("Content-Disposition: form-data; name=\"{0}\"; filename=\"{1}\"\r\n", "fileX.ttl", "fileX.ttl");
                fileItem += "Content-Type: text/turtle";
                fileItem += "\r\n\r\n";
                fileItem += "<urn:a> <urn:b> <urn:c> .";
                fileItem += "\r\n";

                byte[] fileBytes = Encoding.UTF8.GetBytes(fileItem);
                s.Write(fileBytes, 0, fileBytes.Length);

                // terminating boundary
                s.Write(terminatorBytes, 0, terminatorBytes.Length);
                s.Close();
            }

            using (HttpWebResponse createDatabasesResponse = (HttpWebResponse)createDatabasesRequest.GetResponse())
            {
                var rs = createDatabasesResponse.GetResponseStream();

                try
                {
                    if (createDatabasesResponse.ContentEncoding.ToLower().Contains("gzip"))
                    {
                        rs = new GZipStream(rs, CompressionMode.Decompress);
                    }
                    else if (createDatabasesResponse.ContentEncoding.ToLower().Contains("deflate"))
                    {
                        rs = new DeflateStream(rs, CompressionMode.Decompress);
                    }

                    StreamReader Reader = new StreamReader(rs, Encoding.Default);

                    string jsonResponse = Reader.ReadToEnd();
                    CreateResponse cr =  JsonConvert.DeserializeObject<CreateResponse>(jsonResponse);

                    Console.WriteLine(cr.Message);
                }
                finally
                {
                    rs.Close();
                }

            }
        }
    }
}
