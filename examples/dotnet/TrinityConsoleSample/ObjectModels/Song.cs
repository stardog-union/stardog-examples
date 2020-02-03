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
    ///  A music recording that is a single work of music.
    /// </summary>
    [RdfClass(MUSIC.Song)]
    public class Song : Resource
    {

        #region Members

        /// <summary>
        /// The name of an entity.
        /// </summary>
        [RdfProperty(MUSIC.name)]
        public string Name { get; set; }

        /// <summary>
        /// A person or a group of people who participated in the creation of song as a composer or a lyricist.
        /// </summary>
        [RdfProperty(MUSIC.writer)]
        public List<Songwriter> Writers { get; set; }

        /// <summary>
        /// The length of a song in the album expressed in seconds.
        /// </summary>
        [RdfProperty(MUSIC.length)]
        public int Length { get; set; }

        #endregion

        #region Constructors

        public Song(Uri uri) : base(uri) { }

        #endregion
    }
}