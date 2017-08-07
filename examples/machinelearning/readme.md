## Machine Learning

In this tutorial, you'll learn how to use the main machine learning components of Stardog. You'll be using some of the most interesting features of Stardog to solve regression and classification tasks on a movie dataset.

For an overview of all the features and usage syntax, please refer to the official [documentation](http://www.stardog.com/docs/#_machine_learning).

### Data

Before digging into details, let's load the data into Stardog.

```
./stardog-admin db create -n movies movies.ttl
```

The dataset contains information about 6707 movies.
The data itself is very sparse, with movies having varied degrees of detail.
A typical movie, identified by its IMDB ID, contains the following properties:

```
t:tt0118715 :actor n:nm0000422 , n:nm0000114 , n:nm0000313 , n:nm0000194 ;
    :description "\"The Dude\" Lebowski, mistaken for a millionaire Lebowski, seeks restitution for his ruined rug and enlists his bowling buddies to help get it." ;
    rdfs:label "The Big Lebowski" ;
    :boxOffice 17439163 ;
    :author n:nm0001053 , n:nm0001054 ;
    :director n:nm0001054 ;
    :genre "Crime" , "Comedy" , "Mystery" ;
    :contentRating "R" ;
    :copyrightYear 1998 ;
    :rating "8.2"^^xsd:float ;
    :productionCompany c:co0057311 , c:co0030612 ;
    :keyword "death" , "drug" , "nihilism" , "rug" , "white russian" ;
    :language "German" , "English" , "Spanish" , "Hebrew" ;
    :storyline "When \"The Dude\" Lebowski is mistaken for a millionaire Lebowski, two thugs urinate on his rug to coerce him into paying a debt he knows nothing about. While attempting to gain recompense for the ruined rug from his wealthy counterpart, he accepts a one-time job with high pay-off. He enlists the help of his bowling buddy, Walter, a gun-toting Jewish-convert with anger issues. Deception leads to more trouble, and it soon seems that everyone from porn empire tycoons to nihilists want something from The Dude." ;
    :recommendation t:tt0208092 , t:tt0110912 , t:tt0169547 , t:tt0066921 , t:tt0105236 , t:tt0266697 , t:tt1205489 , t:tt0361748 , t:tt0469494 , t:tt0075314 , t:tt0477348 , t:tt0116282 ;
    :metaCritic 69 .
```

### Predicting Average User Rating

First task will be predicting the average user rating given by IMDB users, available with the `:rating` property.
We'll be testing if solely based on a movie's metadata, we can accurately predict what rating an average user will give.

First, let's look at the distribution of ratings in the dataset.

```
./stardog query movies 1-histogram.sparql
```

```
+--------+-------+
| rating | count |
+--------+-------+
| 9      | 13    |
| 8      | 655   |
| 7      | 2328  |
| 6      | 2387  |
| 5      | 1072  |
| 4      | 260   |
| 3      | 57    |
| 2      | 22    |
| 1      | 13    |
+--------+-------+
```

Users have the tendency to give positive ratings overall, with ratings following an asymmetric distribution, skewed towards the 5-8 region.

With this in mind, let's build our first model!

````
prefix agg: <urn:aggregate>
prefix spa: <tag:stardog:api:analytics:>

INSERT {
    graph spa:model {
        :r1 a spa:RegressionModel ;
            spa:arguments (?genres ?contentRating ?storyline ?metaCritic) ;
            spa:predict ?rating ;
            spa:crossValidation 100 ;
            spa:validationMetric spa:mae .
    }
}
WHERE {
        SELECT 
        (agg:spa:set(?genre) as ?genres) 
        ?contentRating
        ?storyline
        ?metaCritic
        ?rating
        {
            ?movie  :rating ?rating .

            OPTIONAL {
                ?movie  :genre ?genre ;
                        :contentRating ?contentRating ;
                        :storyline ?storyline ;
                        :metaCritic ?metaCritic .
            }
        }
        GROUP BY ?movie ?rating ?contentRating ?storyline ?metaCritic
}
````

In this query, we are creating a model named `:r1`, that will predict a movie's average user rating based on its genres, content rating, storyline, and metacritic score. Since rating is a numeric value, this is a regression task, being `:r1` identified as a `spa:RegressionModel`.

The `WHERE` clause selects the data used in the training of the model. There is plenty of freedom in defining the variables given as arguments. They can be missing from some movies, and they can have any datatype, from which Stardog will infer the best way to integrate them in the learning process. For example, storyline is a text field, which will be internally tokenized, while genre is a set of values, which will be independently integrated as categorical features.

Models are automatically evaluated on creation, and by defining the `spa:crossValidation` and `spa:validationMetric` properties, we are defining how that evaluation will happen. In this case, we will be using 100-fold cross validation, using the mean absolute error as score.

```
./stardog query movies 2-simple_model.sparql
```

After creating the model, the evaluation results are stored in the database and can be easily acessed.

```
./stardog query movies 2.1-score.sparql
```

```
+----------------------+
| mean_absolute_error  |
+----------------------+
| 0.7905053493179834   |
+----------------------+
```

We can predict a movie's rating with a 0.79 error margin. Not bad!

Tweaking some of Stardog's learning algorithm parameters through the `spa:parameters` property, decreases the error even further.

```
./stardog query movies 3-tweaked_parameters.sparql
./stardog query movies 3.1-score.sparql
```

```
+---------------------+
| mean_absolute_error |
+---------------------+
| 0.7063162621898929  |
+---------------------+
```

Let's inspect the ratings given by this model for some random movies in the dataset.

```
./stardog query movies 4-predicted_ratings.sparql
```

```
+-----------------------------------------+------------------+------------------------+
|                  title                  |      rating      |    predictedRating     |
+-----------------------------------------+------------------+------------------------+
| "Independence Day"                      | "6.9"^^xsd:float | "6.866226"^^xsd:float  |
| "Raging Bull"                           | "8.3"^^xsd:float | "7.9724197"^^xsd:float |
| "Star Trek V: The Final Frontier"       | "5.4"^^xsd:float | "5.411457"^^xsd:float  |
| "The Handmaiden"                        | "8.1"^^xsd:float | "7.9376"^^xsd:float    |
| "Harry Potter and the Sorcerer's Stone" | "7.5"^^xsd:float | "7.239612"^^xsd:float  |
| "The Princess Bride"                    | "8.1"^^xsd:float | "7.8432794"^^xsd:float |
| "X-Men: First Class"                    | "7.8"^^xsd:float | "7.4362893"^^xsd:float |
| "Central Intelligence"                  | "6.4"^^xsd:float | "6.092653"^^xsd:float  |
| "L.A. Confidential"                     | "8.3"^^xsd:float | "7.9753857"^^xsd:float |
| "Phantasm II"                           | "6.5"^^xsd:float | "6.4561276"^^xsd:float |
| "The Godfather"                         | "9.2"^^xsd:float | "8.723323"^^xsd:float  |
| "Chinatown"                             | "8.2"^^xsd:float | "7.79008"^^xsd:float   |
| "The Conjuring 2"                       | "7.5"^^xsd:float | "7.10889"^^xsd:float   |
| "The Curse of the Jade Scorpion"        | "6.8"^^xsd:float | "6.4890537"^^xsd:float |
| "The Promise"                           | "4.1"^^xsd:float | "5.0445213"^^xsd:float |
| "Thirteen Days"                         | "7.3"^^xsd:float | "6.9887247"^^xsd:float |
| "The Last Days on Mars"                 | "5.5"^^xsd:float | "5.5336046"^^xsd:float |
| "A Room with a View"                    | "7.4"^^xsd:float | "7.3855906"^^xsd:float |
| "Russian Dolls"                         | "7.0"^^xsd:float | "6.7159142"^^xsd:float |
| "You Got Served"                        | "3.5"^^xsd:float | "4.533333"^^xsd:float  |
+-----------------------------------------+------------------+------------------------+
```

Movies further away from the average rating seem to have a bigger error rate, but still a good performance overall.
Which other information would you had to this model to make it even better?


### Generating Movie Recommendations

Our dataset contains information about which movies IMDB recommends as similar in their `People who liked this also liked` box.
Their [algorithm](http://www.imdb.com/help/show_leaf?personalrecommendations) is based on the ratings given by the millions of users in their platform. The assumption is, if users regularly give similar ratings to the same movies, there's high chances of them being somehow similar.

Unfortunately, we don't have this kind of rich data about users. Our challenge will be to create a recommendation system based solely on a movie's metadata.

We are going to solve this problem as a classification task, the objective being predicting if IMDB considers two movies as similar. Since recommendations present in this dataset are very sparse, we need to select which movies are we interested in being able to suggest as recommendations. For the purpose of this tutorial, we took a sample of 100 movies from the original dataset.

```
./stardog query movies 5-top_recommended_movies.sparql
```

Our model is going to be similar to the one previously created for rating prediction.

```
prefix agg: <urn:aggregate>
prefix spa: <tag:stardog:api:analytics:>

INSERT {
    graph spa:model {
        :c1 a spa:ClassificationModel ;
                spa:parameters '-b 22 -l 10 --l1 0.0000001' ;
                spa:arguments (?actors ?writers ?directors ?genres ?producers ?keywords ?languages ?contentRating ?year ?metaCritic ?rating) ;
                spa:predict ?rec .
    }
}
WHERE {
    SELECT         
    (agg:spa:set(?actor) as ?actors) 
    (agg:spa:set(?writer) as ?writers)
    (agg:spa:set(?director) as ?directors)
    (agg:spa:set(?genre) as ?genres)
    (agg:spa:set(?producer) as ?producers)
    (agg:spa:set(?keyword) as ?keywords)
    (agg:spa:set(?language) as ?languages)
    ?contentRating
    ?year
    ?metaCritic
    ?storyline
    ?rating
    ?rec
    {
        ?movie  :recommendation ?recommendation ;
                :rating ?rating .
        ?recommendation rdfs:label ?rec .

        OPTIONAL {
        ?movie  :actor ?actor ;
                :author ?writer ;
                :director ?director ;
                :genre ?genre ;
                :contentRating ?contentRating ;
                :copyrightYear ?year ;
                :productionCompany ?producer ;
                :keyword ?keyword ;
                :language ?language ;
                :storyline ?storyline ;
                :metaCritic ?metaCritic .
        }
        FILTER (?recommendation in (t:tt0137523, t:tt1375666, t:tt0105236, t:tt1228705, t:tt0126029, t:tt1591095, t:tt1637725, t:tt0102926, t:tt0251127, t:tt0133093, t:tt0382932, t:tt1853728, t:tt0389860, t:tt1232829, t:tt1129442, t:tt1300854, t:tt0104431, t:tt0762107, t:tt0327084, t:tt0167260, t:tt0448157, t:tt0816711, t:tt0145487, t:tt0332280, t:tt0364725, t:tt0117060, t:tt0088763, t:tt1285016, t:tt0454876, t:tt2562232, t:tt1877832, t:tt0400717, t:tt0436697, t:tt0114369, t:tt0281358, t:tt0120611, t:tt0266697, t:tt0280590, t:tt0351283, t:tt1320253, t:tt0080684, t:tt0854678, t:tt1454468, t:tt1375670, t:tt0398165, t:tt0211915, t:tt0361862, t:tt0359013, t:tt0901476, t:tt0361748, t:tt0993846, t:tt1631867, t:tt0181689, t:tt0887883, t:tt1033643, t:tt0120737, t:tt0107290, t:tt1606389, t:tt0454848, t:tt0240772, t:tt0765429, t:tt0343818, t:tt0212720, t:tt1632708, t:tt0844471, t:tt3498820, t:tt0077651, t:tt0099685, t:tt0405422, t:tt0073486, t:tt0082971, t:tt0497465, t:tt0167404, t:tt0450278, t:tt1323594, t:tt0120783, t:tt0243155, t:tt1798709, t:tt1489889, t:tt1690953, t:tt1010048, t:tt0337563, t:tt0120660, t:tt0989757, t:tt1277953, t:tt0429591, t:tt0095016, t:tt0388789, t:tt1646987, t:tt0112508, t:tt0415306, t:tt0320691, t:tt0212338, t:tt0382628, t:tt0265086, t:tt0117438, t:tt1392190, t:tt0362227, t:tt0368933, t:tt1431045))
    }
    GROUP BY ?movie ?year ?contentRating ?metaCritic ?storyline ?rating ?rec
}
```

From all the movies in the dataset, we select the ones that are similar to the previously selected 100, and input their properties as arguments.
Our target variable is the movie being recommended, a categorical feature. Therefore, we use a `spa:ClassificationModel`, with some additional (optional) parameters.

```
./stardog query movies 6-recommender_model.sparql
```

Using the `spa:confidence` parameter, this model can be used to generate a weighted list of recommendations for any movie. 
Here are the top 5 recommended movies for [The Big Lebowski](https://www.youtube.com/watch?v=PztgWdMEJdg):

```
./stardog query movies 7-recommended_movies.sparql
```

```
+------------------------+-----------------------+
|     recommendation     |      confidence       |
+------------------------+-----------------------+
| "Burn After Reading"   | 0.6159123182296753    |
| "Inglourious Basterds" | 0.32919785380363464   |
| "Kill Bill: Vol. 1"    | 0.024150144308805466  |
| "Reservoir Dogs"       | 0.021390559151768684  |
| "Amélie"               | 0.0038712737150490284 |
+------------------------+-----------------------+
```

### Future Work

This tutorial touched the basics of machine learning in Stardog. 
For a more compreensive and updated overview, refer to the official [documentation](http://www.stardog.com/docs/#_machine_learning).

If you have any questions or suggestions, drop by our [community forum](https://community.stardog.com/).
Happy Machine Learning!