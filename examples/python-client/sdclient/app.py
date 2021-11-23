import stardog
from pathlib import Path
import pprint

pp = pprint.PrettyPrinter(indent=2)

# specify our endpoint, username, and password
# default values are provided, be sure to change if necessary
conn_details = {
    'endpoint': 'http://localhost:5820',
    'username': 'admin',
    'password': 'admin'
}

# create a new admin connection
with stardog.Admin(**conn_details) as admin:
    # create a new database
    db = admin.new_database('pythondb')
    print('Created db')

    # create a connection to the db
    with stardog.Connection('pythondb', **conn_details) as conn:
        # begin transaction
        conn.begin()
        # add data to transaction from file
        path = str(Path(__file__).parent.resolve() / 'resources/GettingStarted_Music_Data.ttl')
        conn.add(stardog.content.File(path))
        # commit the changes
        conn.commit()

        # SELECT some of the data we just inserted
        pp.pprint(conn.select('SELECT * {?s a :Person} LIMIT 5'))

        # another method of adding data
        conn.begin()
        conn.add(stardog.content.Raw(':Yo_Yo_Ma a :Person', 'text/turtle'))
        conn.commit()

        pp.pprint(conn.select('SELECT ?s { ?s a :Person }'))

        # delete all people from the db
        conn.begin()
        conn.update('DELETE WHERE { ?s ?p ?o }')
        conn.commit()

        pp.pprint(conn.select('SELECT * { ?s ?p ?o }'))

    # drop the db
    # (this is for demonstration purposes, you most likely don't want to do this)
    db.drop()
    print('Dropped db')