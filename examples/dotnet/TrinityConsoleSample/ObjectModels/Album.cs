/*
 * Copyright (c) 2010 - 2019, Stardog Union. <http://www.stardog.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
using example;
using Semiodesk.Trinity;
using System;
using System.Collections.Generic;

namespace TrinityConsoleSample.ObjectModels
{
    /// <summary>
    /// A collection of songs released by an artist on physical or digital medium.
    /// </summary>
    [RdfClass(MUSIC.Album)]
    public class Album : Resource
    {

        #region Members

        /// <summary>
        /// The name of an entity.
        /// </summary>
        [RdfProperty(MUSIC.name)]
        public string Name { get; set; }

        /// <summary>
        /// The release date of an album.
        /// </summary>
        [RdfProperty(MUSIC.date)]
        public DateTime ReleaseDate { get; set; }

        /// <summary>
        /// The artist that performed this album.
        /// </summary>
        [RdfProperty(MUSIC.artist)]
        public Artist Artist { get; set; }

        /// <summary>
        /// A song included in an album.
        /// </summary>
        [RdfProperty(MUSIC.track)]
        public List<Song> Tracks { get; set; }

        #endregion

        #region Constructors

        public Album(Uri uri) : base(uri) { }

        #endregion
    }
}
