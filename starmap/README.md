# Starmap

## Version 0.1

*Starmap* is a Python utility that accepts:

* a list of R style formulae
* a CSV file (defaults to `stdin`) with header and comma delimeter.
* a URN (defaults to "http://api.stardog.com")
* a prefix for that URN (defaults to "")

The only dependency is Pandas.

The formulae are used to describe the graphical relationship between the columns of the CSV. From these formulae, an SMS mapping file is contructed suitable for virtual import of the data in the CSV. The import itself is not carried out in this function. Data types are introduced on the basis of the data in the CSV. Apart from data type transformations, no other functions can be applied to the data in the CSV.

### The formula

A relationship between IRI's is indicated through: `v1 ~ v2`. The direction of the relationship is from left to right, so `v1 -> v2`. In R, the `~` is used to indicate the primary relationship between the dependent variable (LHS) and the independent variables (RHS). Literals are indicated using the dash, `|`. If `v1` has literals `f1, f2, f3`, this is written as `v1 | f1 + f2 + f3` or `v1 | (f1 + f2 + f3)`. The parentheses are just for people who like parentheses. In R, the dash is used to indicate nested variables, which is a reasonable interpretation in this case. 

Assume that a CSV has columns named `v1, v2, v3, f1, f2, f3, f4, f5`. The following are all valid formulae:


* `v1`
* `v1 ~ v2`
* `v1 ~ v2|(f3 + f4)`
* `v1 | f1 + f2 ~ v2 | f3 + f4`
* We can also submit two formulae: `v1 | (f1 + f2) ~ v2 | (f3 + f4)` and `v3 | f5 ~ v2`
  * i.e. iri v1 is related to iri v2; v1 has literals f1 and f2; v2 has literals f3 and f4
  * iri v3 is related to v2. v3 has literal f5. 


Note that the formulae do not infintely recurse. `f1` in the previous example has to be a valid Python variable name that is present in the header line of the CSV (respectively, a column name in the associated Pandas dataframe). `f1` cannot itself be a formula. If users want to add complexity to their formula, they just add another component to the list. The concept is to map the CSV with a list of simple formulae, as opposed to one big, fat, complex formula. 

### Invoking the function 

`cat c360_orders.csv | head | python starmap.py --formula "profile_id ~ order_nbr | order_amt"  "order_nbr ~ item_id | item_amt" --urn "http://api.acme.com/" --prefix "acme" > acme.sms`

*What just happened?*

* Starmap only needs enough of the actual data to determine data types. I pipe my data to `head` and pass that by way of `stdin` to `starmap.py`
* This file has a complicated structure for a CSV. There are 3 columns that I want to graph as IRI nodes: `profile_id`, `order_nbr` and `item_id`. Profiles have orders and orders have items. Orders and items also have some literals, namely `order_amt` for orders and `item_amt` for items. 
* I want to identify my IRI nodes using `http://api.acme.com`, which I will abbreviate to `acme`.
* The resulting sms is directed to `acme.sms`.

**NOTE** If `starmap` is included as a module, call `parseFormulae` with the above arguments, adding `--csv fileName.
csv` for the data.

# TODO

* `parseFormulae` needs the option of getting the data directly from a Pandas data frame. Users can make any data transformations they want to the DF before passing it to `starmap` (which does not do data transformations apart from datatypes).
* There is an issue with `parseFormulae` when multiple formulae are passed at once: the sparql lines can contain duplicates if an IRI is mentioned in both formulae. Possible fixes are:
  * Express every line of sparql-ish SMS as a 3 part phrase with a period at the end. This way, duplicates can be spotted an eliminated. The resulting SMS will be legal, efficient and ugly.
  * Parse everything jointly to ensure no duplicate lines are created, then indent and abbreviate as appropriate.
  * Leave it as it is. The SMS works and the sparqly lines look pretty. However, Stardog has to do more work, which would be onerous for large files. 
* More error trapping and reality checking is needed. For example `x ~ x` is a legal formula, but do we want that? 
* Should possibly allow for non-comma separated CSV's. Maybe.
* Add a term of the form `f1:f2` or `n:m`, where SMS will be created for every literal between f1 and f2 inclusive, or every column number from `n` through `m`. 
* Add an operation to allow for bulk naming. For example: `x {outcome} f1:f10` would create the following sparqly fragment:

```
x rdf:type acme:X ;
    acme:outcome f1, 
                 f2, 
                 f3, 
                 ....,
                 f10 .
```

for those occasions where you totally do not care and can't be bothered to type out the details. I mean, the file might have 500 columns of similar data. 






