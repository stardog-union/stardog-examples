## Machine Learning

In this tutorial, you'll learn how to use the main machine learning components of Stardog. You'll be using some of the most interesting features of Stardog to solve regression and classification tasks on a movie dataset.

For an overview of all the features and usage syntax, please refer to the official [documentation](http://www.stardog.com/docs/#_machine_learning).

### Data

Before digging into details, let's load the data into Stardog.

```
stardog-admin db create -n movies movies.ttl
```

The dataset contains information about 6730 movies.
The data itself is very sparse, with movies having varied degrees of detail.
A typical movie, identified by its IMDB ID, contains the following properties:

```
t:tt0118715 :actor n:nm0000313 , n:nm0000422 , n:nm0000194 , n:nm0000114 ;
	:description "\"The Dude\" Lebowski, mistaken for a millionaire Lebowski, seeks restitution for his ruined rug and enlists his bowling buddies to help get it." ;
	rdfs:label "The Big Lebowski" ;
	:author n:nm0001053 , n:nm0001054 ;
	:director n:nm0001054 ;
	:genre "Mystery" , "Comedy" , "Crime" ;
	:contentRating "R" ;
	:copyrightYear 1998 ;
	:rating "8.2"^^xsd:float ;
	:keyword "death" , "drug" , "nihilism" , "rug" , "white russian" ;
	:language "German" , "English" , "Spanish" , "Hebrew" ;
	:storyline "When \"The Dude\" Lebowski is mistaken for a millionaire Lebowski, two thugs urinate on his rug to coerce him into paying a debt he knows nothing about. While attempting to gain recompense for the ruined rug from his wealthy counterpart, he accepts a one-time job with high pay-off. He enlists the help of his bowling buddy, Walter, a gun-toting Jewish-convert with anger issues. Deception leads to more trouble, and it soon seems that everyone from porn empire tycoons to nihilists want something from The Dude." ;
	:productionCompany c:co0057311 , c:co0030612 ;
	:recommendation t:tt0110912 , t:tt0066921 , t:tt0075314 , t:tt0105236 , t:tt0169547 , t:tt0266697 , t:tt0361748 , t:tt0477348 , t:tt1205489 , t:tt0469494 , t:tt0208092 , t:tt0116282 ;
	:metaCritic 69 .
```

### Predicting Average User Rating

First task will be predicting the average user rating given by IMDB users, available with the `:rating` property.
We'll be testing if solely based on a movie's metadata, we can accurately predict what rating an average user will give.

First, let's look at the distribution of ratings in the dataset.

```
stardog query movies 1-histogram.sparql
```

```
+--------+-------+
| rating | count |
+--------+-------+
| 9      | 12    |
| 8      | 632   |
| 7      | 2296  |
| 6      | 2376  |
| 5      | 1069  |
| 4      | 258   |
| 3      | 57    |
| 2      | 20    |
| 1      | 10    |
+--------+-------+
```

Users have the tendency to give positive ratings overall, with ratings following an asymmetric distribution, skewed towards the 5-8 region.

With this in mind, let's build our first model!

````
prefix : <http://schema.org/>
prefix spa: <tag:stardog:api:analytics:>

INSERT {
    graph spa:model {
        :r1 a spa:RegressionModel ;
            spa:arguments (?genres ?contentRating ?storyline ?metaCritic) ;
            spa:predict ?rating ;
            spa:crossValidation 100 ;
            spa:validationMetric spa:mae ;
            spa:overwrite True .
    }
}
WHERE {
    SELECT 
    (spa:set(?genre) as ?genres) 
    ?contentRating
    ?storyline
    ?metaCritic
    ?rating
    {
        ?movie  :rating ?rating ;
                :genre ?genre ;
                :contentRating ?contentRating ;
                :storyline ?storyline .

        OPTIONAL {
            ?movie  :metaCritic ?metaCritic .
        }
    }
    GROUP BY ?movie ?rating ?contentRating ?storyline ?metaCritic
}
````

In this query, we are creating a model named `:r1`, that will predict a movie's average user rating based on its genres, content rating, storyline, and metacritic score. Since rating is a numeric value, this is a regression task, being `:r1` identified as a `spa:RegressionModel`.

The `WHERE` clause selects the data used in the training of the model. There is plenty of freedom in defining the variables given as arguments. They can be missing from some movies, and they can have any datatype, from which Stardog will infer the best way to integrate them in the learning process. For example, storyline is a text field, which will be internally tokenized, while genre is a set of values, which will be independently integrated as categorical features.

Models are automatically evaluated on creation, and by defining the `spa:crossValidation` and `spa:validationMetric` properties, we are defining how that evaluation will happen. In this case, we will be using 100-fold cross validation, using the mean absolute error as score.

```
stardog query movies 2-simple_model.sparql
```

After creating the model, the evaluation results are stored in the database and can be easily acessed.

```
stardog query movies 2.1-score.sparql
```

```
+---------------------+
| mean_absolute_error |
+---------------------+
| 1.025538226920225E0 |
+---------------------+
```

We can accurately predict a movie's rating with a one star error margin. Not bad!

Using [hyperparameter optimization](https://www.stardog.com/docs/#_hyperparameter_optimization), we can decrease the error even further.

```
stardog query movies 3-tweaked_parameters.sparql
stardog query movies 3.1-score.sparql
```

```
+----------------------+
| mean_absolute_error  |
+----------------------+
| 6.552457129602058E-1 |
+----------------------+
```

Let's inspect the ratings given by this model for some random movies in the dataset.

```
stardog query movies 4-predicted_ratings.sparql
```

```
+------------------------------+------------------+------------------------+
|            title             |      rating      |    predictedRating     |
+------------------------------+------------------+------------------------+
| "The Straight Story"         | "8.0"^^xsd:float | "7.6452527"^^xsd:float |
| "Slacker"                    | "7.1"^^xsd:float | "6.8519254"^^xsd:float |
| "Before Night Falls"         | "7.3"^^xsd:float | "7.2929173"^^xsd:float |
| "White House Down"           | "6.4"^^xsd:float | "6.478671"^^xsd:float  |
| "Click"                      | "6.4"^^xsd:float | "6.3205924"^^xsd:float |
| "Beauty and the Beast"       | "8.0"^^xsd:float | "8.010926"^^xsd:float  |
| "Ran"                        | "8.3"^^xsd:float | "8.270319"^^xsd:float  |
| "A Time to Kill"             | "7.4"^^xsd:float | "7.0004272"^^xsd:float |
| "The Lucky One"              | "6.5"^^xsd:float | "6.6651726"^^xsd:float |
| "The Pacifier"               | "5.5"^^xsd:float | "5.2455955"^^xsd:float |
| "eXistenZ"                   | "6.8"^^xsd:float | "6.5661464"^^xsd:float |
| "Saw"                        | "7.7"^^xsd:float | "7.0539207"^^xsd:float |
| "Paranormal Activity 3"      | "5.8"^^xsd:float | "5.373523"^^xsd:float  |
| "The Matrix Revolutions"     | "6.7"^^xsd:float | "6.189322"^^xsd:float  |
| "Before the Fall"            | "7.5"^^xsd:float | "7.5956726"^^xsd:float |
| "New York Minute"            | "4.8"^^xsd:float | "4.8818574"^^xsd:float |
| "Terminator 2: Judgment Day" | "8.5"^^xsd:float | "8.064457"^^xsd:float  |
| "Diary of the Dead"          | "5.7"^^xsd:float | "5.7808275"^^xsd:float |
| "The Last Days of Disco"     | "6.6"^^xsd:float | "6.6486125"^^xsd:float |
| "Pitch Black"                | "7.1"^^xsd:float | "6.6474075"^^xsd:float |
+------------------------------+------------------+------------------------+
```

Movies further away from the average rating seem to have a bigger error rate, but still a good performance overall.
Which other information would you had to this model to make it even better?


### Generating Movie Recommendations

Our dataset contains information about which movies IMDB recommends as similar in their `People who liked this also liked` box.
Their [algorithm](http://www.imdb.com/help/show_leaf?personalrecommendations) is based on the ratings given by the millions of users in their platform. The assumption is, if users regularly give similar ratings to the same movies, there's high chances of them being somehow similar.

Unfortunately, we don't have this kind of rich data about users. Our challenge will be to create a recommendation system based solely on a movie's metadata.

We are going to solve this problem as a classification task, the objective being predicting if IMDB considers two movies as similar. Since recommendations present in this dataset are very sparse, we need to select which movies are we interested in being able to suggest as recommendations. For the purpose of this tutorial, we took a sample of 100 movies from the original dataset.

```
stardog query movies 5-top_recommended_movies.sparql
```

Our model is going to be similar to the one previously created for rating prediction.

```
prefix : <http://schema.org/>
prefix t: <http://www.imdb.com/title/>
prefix spa: <tag:stardog:api:analytics:>

INSERT {
    graph spa:model {
        :c1 a spa:ClassificationModel ;
                spa:parameters [
                    spa:b 22 ;
                    spa:l 1 ;
                    spa:l1 0.000001
                ] ;
                spa:arguments (?actors ?writers ?directors ?genres ?producers ?keywords ?languages ?contentRating ?year ?metaCritic ?rating) ;
                spa:predict ?rec ;
                spa:overwrite True .
    }
}
WHERE {
    SELECT         
    (spa:set(?actor) as ?actors) 
    (spa:set(?writer) as ?writers)
    (spa:set(?director) as ?directors)
    (spa:set(?genre) as ?genres)
    (spa:set(?producer) as ?producers)
    (spa:set(?keyword) as ?keywords)
    (spa:set(?language) as ?languages)
    ?contentRating
    ?year
    ?metaCritic
    ?storyline
    ?rating
    ?rec
    {
        ?movie  :recommendation ?recommendation ;
                :actor ?actor ;
                :author ?writer ;
                :director ?director ;
                :genre ?genre ;
                :contentRating ?contentRating ;
                :copyrightYear ?year ;
                :keyword ?keyword ;
                :language ?language ;
                :storyline ?storyline ;
                :rating ?rating .
        ?recommendation rdfs:label ?rec .

        OPTIONAL {
            ?movie  :productionCompany ?producer .
        }
        OPTIONAL {
            ?movie  :metaCritic ?metaCritic .
        }

        FILTER (?recommendation in (t:tt0137523, t:tt1375666, t:tt0105236, t:tt1228705, t:tt0126029, t:tt1591095, t:tt1637725, t:tt0102926, t:tt0251127, t:tt0133093, t:tt0382932, t:tt1853728, t:tt0389860, t:tt1232829, t:tt1129442, t:tt1300854, t:tt0104431, t:tt0762107, t:tt0327084, t:tt0167260, t:tt0448157, t:tt0816711, t:tt0145487, t:tt0332280, t:tt0364725, t:tt0117060, t:tt0088763, t:tt1285016, t:tt0454876, t:tt2562232, t:tt1877832, t:tt0400717, t:tt0436697, t:tt0114369, t:tt0281358, t:tt0120611, t:tt0266697, t:tt0280590, t:tt0351283, t:tt1320253, t:tt0080684, t:tt0854678, t:tt1454468, t:tt1375670, t:tt0398165, t:tt0211915, t:tt0361862, t:tt0359013, t:tt0901476, t:tt0361748, t:tt0993846, t:tt1631867, t:tt0181689, t:tt0887883, t:tt1033643, t:tt0120737, t:tt0107290, t:tt1606389, t:tt0454848, t:tt0240772, t:tt0765429, t:tt0343818, t:tt0212720, t:tt1632708, t:tt0844471, t:tt3498820, t:tt0077651, t:tt0099685, t:tt0405422, t:tt0073486, t:tt0082971, t:tt0497465, t:tt0167404, t:tt0450278, t:tt1323594, t:tt0120783, t:tt0243155, t:tt1798709, t:tt1489889, t:tt1690953, t:tt1010048, t:tt0337563, t:tt0120660, t:tt0989757, t:tt1277953, t:tt0429591, t:tt0095016, t:tt0388789, t:tt1646987, t:tt0112508, t:tt0415306, t:tt0320691, t:tt0212338, t:tt0382628, t:tt0265086, t:tt0117438, t:tt1392190, t:tt0362227, t:tt0368933, t:tt1431045))
    }
    GROUP BY ?movie ?year ?contentRating ?metaCritic ?storyline ?rating ?rec
}
```

From all the movies in the dataset, we select the ones that are similar to the previously selected 100, and input their properties as arguments.
Our target variable is the movie being recommended, a categorical feature. Therefore, we use a `spa:ClassificationModel`, with some additional (optional) parameters.

```
stardog query movies 6-recommender_model.sparql
```

Using the `spa:confidence` parameter, this model can be used to generate a weighted list of recommendations for any movie. 
Here are the top 5 recommended movies for [The Big Lebowski](https://www.youtube.com/watch?v=PztgWdMEJdg):

```
stardog query movies 7-recommended_movies.sparql
```

```
+------------------------+-----------------------+
|     recommendation     |      confidence       |
+------------------------+-----------------------+
| "Inglourious Basterds" | 1.645991951227188E-1  |
| "Kill Bill: Vol. 1"    | 1.3105767965316772E-1 |
| "Reservoir Dogs"       | 1.2146846204996109E-1 |
| "Burn After Reading"   | 8.05027186870575E-2   |
| "Ted"                  | 7.016666978597641E-2  |
+------------------------+-----------------------+
```


### Finding Similar Movies

A simpler, and unsupervised, way of generating recommendations is by finding movies with similar features.
This can be achieved by using a `SimilarityModel`.

```
prefix : <http://schema.org/>
prefix spa: <tag:stardog:api:analytics:>

INSERT {
    graph spa:model {
        :s1 a spa:SimilarityModel ;
            spa:arguments (?genres ?directors ?authors ?producers ?metaCritic) ;
            spa:predict ?movie ;
            spa:overwrite True .
    }
}
WHERE {
    SELECT 
    (spa:set(?genre) as ?genres) 
    (spa:set(?director) as ?directors)
    (spa:set(?author) as ?authors)
    (spa:set(?producer) as ?producers)
    ?metaCritic
    ?movie
    {
        ?movie  :genre ?genre ;
                :director ?director ;
                :author ?author .

        OPTIONAL {
            ?movie  :productionCompany ?producer .
        }

        OPTIONAL {
            ?movie  :metaCritic ?metaCritic .
        }
    }
    GROUP BY ?movie ?metaCritic 
}
```

This model will find similar movies based on their genres, directors, authors, producers, and MetaCritic score. 

```
stardog query movies 8-similarity_model.sparql
```

Using this model, we can find the most similar movies to `The Big Lebowski`, aka the ones which share the most amount of features.

```
stardog query movies 9-similarity_search.sparql
```

```
+----------------------------+----------------------+
|     similarMovieLabel      |      confidence      |
+----------------------------+----------------------+
| "The Big Lebowski"         | 9.999999999999998E-1 |
| "Fargo"                    | 9.996443676337468E-1 |
| "Blood Simple."            | 9.996332068990889E-1 |
| "The Man Who Wasn't There" | 9.996019945613324E-1 |
| "Barton Fink"              | 9.99580272822665E-1  |
+----------------------------+----------------------+
```

The most similar movie is, obviously, the movie itself, followed by other movies from the same crew.


### Future Work

This tutorial touched the basics of machine learning in Stardog. 
For a more comprehensive and updated overview, refer to the official [documentation](http://www.stardog.com/docs/#_machine_learning).

If you have any questions or suggestions, drop by our [community forum](https://community.stardog.com/).
Happy Machine Learning!
