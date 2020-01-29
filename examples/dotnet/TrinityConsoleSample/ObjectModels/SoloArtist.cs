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

namespace TrinityConsoleSample.ObjectModels
{
    // NOTE: in the ontology SoloArtist is a sub class of Artist AND Person. In C#,
    // we can handle that by inheriting from one of the concrete classes and interfaces which
    // represent the other classes

    /// <summary>
    /// A single person who is a musical artist.
    /// </summary>
    [RdfClass(MUSIC.SoloArtist)]
    class SoloArtist : Person, IArtist
    {
        #region Constructors

        public SoloArtist(Uri uri) : base(uri) { }

        #endregion
    }
}
