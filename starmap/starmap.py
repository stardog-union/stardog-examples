#! //usr//bin/env python

import pandas as pd
import re

class parseException(BaseException):
    def __init__(self, msg):
        super().__init__(msg)

def flatten(list_of_lists):
    flat_list = [item for sub_list in list_of_lists for item in sub_list]
    return flat_list

def getDataType(var, df):
    # assume that we knnow var is in df
    data_type = str(df.dtypes[var])

    if 'object' in data_type:
        return 'xsd:string'
    elif 'datetime' in data_type:
        return 'xsd:datetime'
    elif 'date' in data_type:
        return 'xsd:date'
    elif 'float' in data_type:
        return 'xsd:decimal'
    elif 'int' in data_type:
        return 'xsd:integer'
    elif 'bool' in data_type:
        return 'xsd:boolean'
    else:
        raise parseException                    

def snakeCamel(svar):
    svar = svar.strip()
    svar = svar.split("_")
    if len(svar) == 1:
        return(svar[0].lower())
    else:
        result = "".join([s.title() for s in svar])
        result = result[:1].lower() + result[1:]
        return(result)

def snakePascal(svar):
    svar = svar.strip()
    svar = svar.split("_")
    if len(svar) == 1:
        return(svar[0].title())
    else:
        result = "".join([s.title() for s in svar])
        result = result[:1].upper() + result[1:]
        return(result)


def checkTokenRange(p, df):
    # the token should be either a numeric range or a column name of the data frame
    # Either way, we need to return a list.
    p = p.strip()
    column_names = df.columns.to_list()
    check = p.split(':')

    if len(check) == 2:
        rangeRegex = re.compile(r'(\d)*:(\d)*')
        test = rangeRegex.search(p)
        if test and (test.group() == p):
            column_range = [int(t) for t in check]
            #if any([col >= df.shape[1] for col in column_range]):
                #raise parseException("Column range out of bounds")
        else:
            lower = column_names.index(checkTokeninDF(check[0], df))
            upper = column_names.index(checkTokeninDF(check[1], df)) + 1
            if lower >= upper:
                raise parseException("Field names to not constitute a valid range")
            column_range = [lower, upper]           
        
        response =  column_names[column_range[0]:column_range[1]]  
    else:
        response = [checkTokeninDF(p, df)]
    return response                  

def processPlus(p, df):

    check = p.split("+")
    trim_check = [c.strip() for c in check]
    response = []
    for token in trim_check:
        response.append(checkTokenRange(token, df))

    return flatten(response) 
 

def checkDash(formula):
    response = None
    check = formula.split("|")
    
    if len(check) == 1:
        return response
    elif len(check) == 2:
        response = [c.strip() for c in check]
        return response 
    else:
        raise parseException("Something wrong here")        

def checkTilde(formula):
    
    check = formula.split("~")
    output = {"lhs": None, "rhs": None}
    if len(check) == 1:
        output['lhs'] = check[0].strip()
    elif len(check) == 2:
        temp = [c.strip() for c in check]
        output['lhs'] = temp[0]
        output['rhs'] = temp[1]
    else:
        raise parseException("A formula can only have one ~ symbol")

    return output    

def checkTokeninDF(p, df):
    # Must be either a column name from the df, or something of the form #name
    p = p.strip()
    if p[0] == '#':
        return p
    elif p in df.columns:
        return p
    else:
        raise parseException("Token not in dataframe namespace")  

def process_partial(p, df):
    check =  checkDash(p)
    if check:
        output = {'lhs': checkTokeninDF(check[0], df)}
        output['rhs'] = processPlus(check[1], df)
        return output   
    else:
        p = checkTokeninDF(p, df)
        return {'lhs': p, 'rhs': None}
       

def process_formula(formula, df):
    partials = checkTilde(formula)

    output = {}
    output['lhs'] = process_partial(partials['lhs'], df)
    if partials['rhs']:
        output['rhs'] = process_partial(partials['rhs'], df)
    else:
        output['rhs'] = None    
           
    return output

# Some utility functions for class Starmap

def process_literals(literals, df, urn, prefix, node_class):
    # Find the data type, create a binding to do the relevant data transformation
    # Add lines of sparql
    # Assume that the first line with the node_iri has already been written
    sparql = ""
    bindings = ""
    onto = ""
    for l in literals:
        sparql += ' ; \n' # finish off the previous line
        data_transform = getDataType(l, df)
        l_name = '?' + l + '_tr'
        property_name = snakeCamel(l)

        sparql += '\t' + prefix + ":" + property_name + " " + l_name
        bindings += 'BIND(' + data_transform + '(?' + l + ') as ' + l_name + ') \n'  
        onto += ( '<' + urn + property_name + '>' + " " + 'a owl:DatatypeProperty ; \n' \
            '\t' + " " + 'rdfs:label' + " " + "'" + property_name + "'" +  "; \n" \
                '\t' + " " + 'rdfs:domain' + " " + '<' + urn + node_class + '>' + " ; \n" \
                    '\t' + " " + 'rdfs:range' + " " + data_transform + " . \n"
        )
    sparql += ' . \n'    
    output = {"sparql": sparql, "bindings": bindings, "onto": onto}
    return output    

def process_side(side, df, urn, node_iri_list):
    # side is d['lhs'] or d['rhs']
    # node_iri_list is a list that gets modified as a side effect.
    # and dictionary side gets modified if the node looks like #varname and we are going to use _ROW_NUMBER_
    # I hate myself for this, but there it is ....
    sparql = ""
    bindings = ""
    onto = ""
    node = side['lhs']
    if node[0] == '#':
        node = node[1:] # strip off the hashtag
        node_binding = '_ROW_NUMBER_'
        side['lhs'] = node # The hashtag is gone now
    else:
        node_binding = node    
    literals = side['rhs']
    node_iri = '?' + node + '_iri'
    if node in node_iri_list:
        if literals:
            data_transform = getDataType(literals[0], df)
            l_name = '?' + literals[0] + '_tr'
            property_name = snakeCamel(literals[0])
            node_class = snakePascal(node)

            sparql += node_iri + " " + prefix + ":" + property_name + " " + l_name
            bindings += 'BIND(' + data_transform + '(?' + literals[0] + ') as ' + l_name + ') \n'  
            onto += ( '<' + urn + property_name + '>' + " " + 'a owl:DatatypeProperty ; \n' \
            '\t' + " " + 'rdfs:label' + " " + "'" + property_name + "'" + " ; \n" \
                '\t' + " " + 'rdfs:domain' + " " + '<' + urn + node_class + '>' + " ; \n" \
                    '\t' + " " + 'rdfs:range' + " " + data_transform + " . \n"
                )
            if len(literals) > 1:
                processed_strings = process_literals(literals[1:], df, urn, prefix, node_class)
                sparql += processed_strings['sparql']
                bindings += processed_strings['bindings']
            else:
                sparql += ' . \n'    
#         else:
#             sparql += ' . \n'        

    else:
        node_iri_list.append(node)
        node_class = snakePascal(node)
        sparql += node_iri + " " + "a" + " " + prefix + ':' + node_class + " ; \n"
        sparql += '\t' + 'rdfs:label' + " " + "?" + node 
        bindings += 'BIND(TEMPLATE("' + urn + node + '_{' + node_binding + '}") as ' + node_iri + ')' + '\n'
        onto += (  '<' + urn + node_class + '>' + " " + 'a owl:Class ; \n' \
                '\trdfs:label' + " " + "'" + node_class + "'" + ' . \n' ) 
                           
    # Process literals, if they exist    

        if literals:
            processed_strings = process_literals(literals, df, urn, prefix, node_class)
            sparql += processed_strings['sparql']
            bindings += processed_strings['bindings']
            onto += processed_strings['onto']
            
        else:
            sparql += " . \n"

    output = {'sparql': sparql, 'bindings': bindings, 'onto': onto}
    return output        

class Starmap:

    def __init__(self, formulae, df, urn, prefix):
        if type(formulae) == str:
            formulae = [formulae]
        elif type(formulae) != list:
            raise parseException("formula must be a string or a list of strings")
        
        processed_formulae = []
        for formula in formulae:
            processed_formulae.append(process_formula(formula, df))  

        sparql = ""
        bindings = ""
        onto = ( "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + ' . \n' +
        "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + ' . \n' +
        "@prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + ' . \n' +
        "@prefix owl: <http://www.w3.org/2002/07/owl#>" + ' . \n' +
        "@prefix stardog: <tag:stardog:api:>" + ' . \n' +
        "@prefix " + prefix + ": <" + urn + '> . \n\n' )

        node_iri_list = [] # in case we see the same node twice in the list of formulae
        for d in processed_formulae:
            # each dictionary represents a formulae
            # Process the IRI node on the LHS. It will always exist
            left_side = d['lhs']
            output = process_side(left_side, df, urn, node_iri_list)
            sparql += output['sparql'] 
            bindings += output['bindings']
            onto += output['onto']

            # Now let's hold that thought and process the right hand side
            right_side = d['rhs']
            if right_side:
                output = process_side(right_side, df, urn, node_iri_list)
                sparql += output['sparql'] 
                bindings += output['bindings']
                onto += output['onto']

                # And finally, connect the LHS to the RHS
                lhs_node_iri = '?' + left_side['lhs'] + '_iri'
                right_node = right_side['lhs']
                right_node_iri = '?' + right_node + '_iri'
                relation_name = "has" + snakePascal(right_node)
                sparql += lhs_node_iri + " " + prefix + ":" + relation_name + " " + right_node_iri + " . \n\n" 
                onto += (
                     '<' + urn + relation_name + '>' + " " + 'a owl:ObjectProperty ; \n' \
                        '\t' + " " + 'rdfs:label' + " " + "'" + relation_name + "'" + " ; \n" \
                            '\t' + " " + 'rdfs:domain' + " " + '<' + urn + snakePascal(left_side['lhs']) + '>' + " ; \n" \
                                '\t' + " " + 'rdfs:range' + " " + '<' + urn + snakePascal(right_node) + ">" + " . \n"
                )
                
                
                         

        theUsual = ( "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + '\n' +
        "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + '\n' +
        "prefix xsd: <http://www.w3.org/2001/XMLSchema#>" + '\n' +
        "prefix owl: <http://www.w3.org/2002/07/owl#>" + '\n' +
        "prefix stardog: <tag:stardog:api:>" + '\n' +
        "prefix " + prefix + ": <" + urn + '> \n' )

        theToBit = ("MAPPING" + "\n" + "FROM CSV {\n}\nTO {")
        theWhereBit = "\nWHERE {\n"
        
        self.sms = theUsual + '\n' + theToBit + '\n' + sparql + '\n}' + theWhereBit + bindings +'\n}'        
        self.onto = onto



if __name__ == "__main__":

    import sys
    import argparse

    parser = argparse.ArgumentParser(description = "Use an R style formula to create an SMS mapping for the supplied CSV file")

    parser.add_argument("--formula", nargs='+', help = """a valid formula uses the header names of the supplied CSV and symbols ~, |, + and :.
        example: f1 | (f2 + f3 + f4) ~ f5 | f6 + f7:f10 """)

    parser.add_argument("--input", default = "", help = "Enter the filename of the CSV file")   
    parser.add_argument("--urn", default = "http://api.stardog.com/", 
        help = "Full URN to use for IRI's defined from the input file")
    parser.add_argument("--prefix", default = "", help = "Prefix to abbreviate the URN")

    args = parser.parse_args() 

    fileName = args.input
    if fileName:
        df =  pd.read_csv(fileName)
        temp = fileName.split('.')
        smsFilename = temp[0] + '.sms'
        ontoFilename = temp[0] + '_onto.ttl'
    else:
        df = pd.read_csv(sys.stdin) 
        smsFilename = 'mapping.sms'
        ontoFilename = 'onto.ttl'     

    formula = args.formula
    urn = args.urn
    prefix = args.prefix

    formulaObj = Starmap(formula, df, urn, prefix)
   
    smsFile = open(smsFilename, 'w')
    smsFile.write(formulaObj.sms)
    smsFile.close()
    ontoFile = open(ontoFilename, 'w')
    ontoFile.write(formulaObj.onto)
    ontoFile.close()

 

    
