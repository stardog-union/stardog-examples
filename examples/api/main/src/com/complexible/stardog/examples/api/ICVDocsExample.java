/*
 * Copyright (c) 2010-2015 Clark & Parsia, LLC. <http://www.clarkparsia.com>
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

package com.complexible.stardog.examples.api;

import java.util.Iterator;

import com.complexible.common.base.CloseableIterator;
import com.complexible.common.openrdf.model.Models2;
import com.complexible.common.openrdf.util.ExpressionFactory;
import com.complexible.common.openrdf.util.ModelBuilder;
import com.complexible.common.openrdf.util.OWL2;
import com.complexible.common.openrdf.vocabulary.Vocabulary;
import com.complexible.common.rdf.model.Values;
import com.complexible.stardog.ContextSets;
import com.complexible.stardog.Stardog;
import com.complexible.stardog.StardogException;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import com.complexible.stardog.icv.Constraint;
import com.complexible.stardog.icv.ConstraintFactory;
import com.complexible.stardog.icv.ConstraintViolation;
import com.complexible.stardog.icv.ICV;
import com.complexible.stardog.icv.api.ICVConnection;
import com.google.common.base.Strings;
import org.openrdf.model.IRI;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.query.BindingSet;

import static com.complexible.common.openrdf.util.ExpressionFactory.all;
import static com.complexible.common.openrdf.util.ExpressionFactory.and;
import static com.complexible.common.openrdf.util.ExpressionFactory.cardinality;
import static com.complexible.common.openrdf.util.ExpressionFactory.dataProperty;
import static com.complexible.common.openrdf.util.ExpressionFactory.domain;
import static com.complexible.common.openrdf.util.ExpressionFactory.functionalProperty;
import static com.complexible.common.openrdf.util.ExpressionFactory.hasValue;
import static com.complexible.common.openrdf.util.ExpressionFactory.inverse;
import static com.complexible.common.openrdf.util.ExpressionFactory.max;
import static com.complexible.common.openrdf.util.ExpressionFactory.min;
import static com.complexible.common.openrdf.util.ExpressionFactory.namedClass;
import static com.complexible.common.openrdf.util.ExpressionFactory.objectProperty;
import static com.complexible.common.openrdf.util.ExpressionFactory.or;
import static com.complexible.common.openrdf.util.ExpressionFactory.propertyList;
import static com.complexible.common.openrdf.util.ExpressionFactory.range;
import static com.complexible.common.openrdf.util.ExpressionFactory.some;
import static com.complexible.common.openrdf.util.ExpressionFactory.subClassOf;
import static com.complexible.common.openrdf.util.ExpressionFactory.subPropertyOf;

/**
 * <p>Source code for the examples in the Stardog ICV documentation.</p>
 *
 * @author Michael Grove
 * @version 4.0
 * @since 0.7
 */
public class ICVDocsExample {

	public static void main(String[] args) throws Exception {
		// First need to initialize the Stardog instance which will automatically start the embedded server.
		Stardog aStardog = Stardog.builder().create();

		try {
			// create a database for the example (if there is already a database with such a name,
			// drop it first)
			try (AdminConnection dbms = AdminConnectionConfiguration.toEmbeddedServer()
			                                                        .credentials("admin", "admin")
			                                                        .connect()) {
				if (dbms.list().contains("testICVDocs")) {
					dbms.drop("testICVDocs");
				}

				dbms.disk("testICVDocs").create();

				// obtain a connection to the database
				try (Connection aConn = ConnectionConfiguration
					                        .to("testICVDocs")                // the name of the db to connect to
					                        .reasoning(true)                // need reasoning for ICV
					                        .credentials("admin", "admin")  // credentials to use while connecting
					                        .connect()) {                        // now open the connection

					// now we create a validator to use
					final ICVConnection aValidator = aConn.as(ICVConnection.class);

					// before we dive into the examples, lets create the concepts and data we'll be using in the examples
					final Vocabulary aVocab = new Vocabulary("http://www.semanticweb.org/company.owl#");

					final IRI Manager = aVocab.term("Manager");
					final IRI Employee = aVocab.term("Employee");
					final IRI Project = aVocab.term("Project");
					final IRI Project_Leader = aVocab.term("Project_Leader");
					final IRI Supervisor = aVocab.term("Supervisor");
					final IRI Department = aVocab.term("Department");
					final IRI US_Government_Agency = aVocab.term("US_Government_Agency");

					final IRI is_responsible_for = aVocab.term("is_responsible_for");
					final IRI is_supervisor_of = aVocab.term("is_supervisor_of");
					final IRI receives_funds_from = aVocab.term("receives_funds_from");
					final IRI dob = aVocab.term("dob");
					final IRI number = aVocab.term("number");
					final IRI supervises = aVocab.term("supervises");
					final IRI works_on = aVocab.term("works_on");
					final IRI works_in = aVocab.term("works_in");
					final IRI manages = aVocab.term("manages");
					final IRI name = aVocab.term("name");
					final IRI nationality = aVocab.term("nationality");

					final IRI NASA = aVocab.term("NASA");
					final IRI Alice = aVocab.term("Alice");
					final IRI Andy = aVocab.term("Andy");
					final IRI Jose = aVocab.term("Jose");
					final IRI Heidi = aVocab.term("Heidi");
					final IRI Diego = aVocab.term("Diego");
					final IRI Maria = aVocab.term("Maria");
					final IRI Bob = aVocab.term("Bob");
					final IRI Esteban = aVocab.term("Esteban");
					final IRI Lucinda = aVocab.term("Lucinda");
					final IRI Isabella = aVocab.term("Isabella");
					final IRI MyProject = aVocab.term("MyProject");
					final IRI MyProjectFoo = aVocab.term("MyProjectFoo");
					final IRI MyProjectBar = aVocab.term("MyProjectBar");
					final IRI MyProjectBaz = aVocab.term("MyProjectBaz");
					final IRI MyDepartment = aVocab.term("MyDepartment");
					final IRI MyDepartment1 = aVocab.term("MyDepartment1");

					System.out.println();

					// the GraphBuilder we'll use to create our Aboxes for the example
					final ModelBuilder aBuilder = new ModelBuilder();

					System.out.println("(1) Subsumption Constraints");
					System.out.println(Strings.repeat("-", 25) + "\n");

					// Managers must be employees
					Constraint aSubConstraint = ConstraintFactory.constraint(subClassOf(Manager, Employee));

					// lets start by adding this constraint to our database
					addConstraint(aValidator, aSubConstraint);

					// we'll create our initial invalid Abox
					aBuilder.instance(Manager, Alice); // Alice is a Manager

					// and add that to stardog
					insert(aConn, aBuilder.model());

					System.out.println("This ABox is not valid, Alice is violating our constraint because she is not an Employee...");
					printValidity(aValidator);

					// we know we're missing that Alice is an Employee, so lets add that to our database
					insert(aConn, Models2.newModel(statement(Alice, RDF.TYPE, Employee)));

					System.out.println("But now that we've stated that Alice is an Employee, we're valid...");
					printValidity(aValidator);

					// clear our builder state
					aBuilder.reset();

					// and clear the database
					clear(aValidator);

					System.out.println("(2) Domain-Range constraints: Only project leaders can be responsible for projects");
					System.out.println(Strings.repeat("-", 25) + "\n");

					Constraint aDomainConstraint = ConstraintFactory.constraint(domain(is_responsible_for, Project_Leader));
					Constraint aRangeConstraint = ConstraintFactory.constraint(range(is_responsible_for, Project));

					addConstraint(aValidator, aDomainConstraint, aRangeConstraint);

					// lets create our initial invalid abox
					aBuilder.instance(Project, MyProject);
					aBuilder.instance(null /* no type */, Alice)
					        .addProperty(is_responsible_for, MyProject);

					// add the invalid data into Stardog
					insert(aConn, aBuilder.model());

					System.out.println("We should see that Alice is violating the domain constraint that she is a Project_Leader...");
					printValidity(aValidator);

					// next example of an invalid abox
					// first remove the old invalid data
					remove(aConn, aBuilder.model());

					// a different invalid abox.  MyProject is not typed as a Project
					aBuilder.reset();
					aBuilder.instance(Project_Leader, Alice)
					        .addProperty(is_responsible_for, MyProject);

					insert(aConn, aBuilder.model());

					System.out.println("Now we should see that MyProject is invalid for the range constraint...");
					printValidity(aValidator);

					// remove invalid data
					remove(aConn, aBuilder.model());

					// create a valid abox
					aBuilder.reset();
					aBuilder.instance(Project, MyProject);
					aBuilder.instance(Project_Leader, Alice)
					        .addProperty(is_responsible_for, MyProject);

					insert(aConn, aBuilder.model());

					System.out.println("And with a complete ABox with all the correct domain and range restrictions met, we're valid...");
					printValidity(aValidator);

					aBuilder.reset();
					clear(aValidator);
					System.out.println("(4) Each date of birth must be a date");
					System.out.println(Strings.repeat("-", 25) + "\n");

					// all dates of birth must be dates.  Note that we're explicitly stating that dob is a dataProperty
					// if this is not specified, the constraint will not be interpreted correctly.
					Constraint aRangeConstraint2 = ConstraintFactory.constraint(range(dataProperty(dob), XMLSchema.DATE));

					addConstraint(aValidator, aRangeConstraint2);

					aBuilder.instance(null /* no type */, Bob)
					        .addProperty(dob, "1970-01-01");

					insert(aConn, aBuilder.model());

					System.out.println("We should see that Bob is violating the range constraint...");
					printValidity(aValidator);

					remove(aConn, aBuilder.model());
					aBuilder.reset();

					// now we'll constraint a valid abox
					aBuilder.instance(null /* no type */, Bob)
					        .addProperty(dob, Values.literal("1970-01-01", XMLSchema.DATE));

					insert(aConn, aBuilder.model());

					System.out.println("Now that Bob's dob is typed, we're valid...");
					printValidity(aValidator);

					clear(aValidator);
					aBuilder.reset();
					System.out.println("(5) Participation constraints: Each supervistor must supervise at least ONE (1) employee");
					System.out.println(Strings.repeat("-", 25) + "\n");

					Constraint aSuperviseConstraint = ConstraintFactory.constraint(subClassOf(Supervisor, some(supervises, Employee)));

					addConstraint(aValidator, aSuperviseConstraint);

					aBuilder.instance(Supervisor, Alice);

					insert(aConn, aBuilder.model());

					System.out.println("We are invalid because Alice is a Supervisor but supervises no one...");
					printValidity(aValidator);

					remove(aConn, aBuilder.model());
					aBuilder.reset();

					aBuilder.instance(Supervisor, Alice)
					        .addProperty(supervises, Bob);

					insert(aConn, aBuilder.model());

					System.out.println("This is still invalid, Alice is a Supervisor and does supervise someone, but that individual is not known to be an employee...");
					printValidity(aValidator);

					// assert that Bob is an employee
					insert(aConn, Models2.newModel(statement(Bob, RDF.TYPE, Employee)));

					System.out.println("We asserted that Bob is an Employee, so we're ok now...");
					printValidity(aValidator);

					clear(aValidator);
					aBuilder.reset();
					System.out.println("(6) Each project must have a valid project number");
					System.out.println(Strings.repeat("-", 25) + "\n");

					Constraint aValidProjectNumberConstraint = ConstraintFactory.constraint(subClassOf(Project, some(number,
					                                                                                                 ExpressionFactory.Datatypes.Integer
						                                                                                                 .minInclusive(Values.literal("0", XMLSchema.INTEGER))
						                                                                                                 .maxExclusive(Values
							                                                                                                               .literal("5000", XMLSchema.INTEGER)))));

					addConstraint(aValidator, aValidProjectNumberConstraint);

					System.out.println("This is ok, it's not typed as a project...");
					aBuilder.instance(OWL2.NAMED_INDIVIDUAL, MyProject);
					insert(aConn, aBuilder.model());

					printValidity(aValidator);

					remove(aConn, aBuilder.model());
					aBuilder.reset();

					System.out.println("But this is not valid: a project w/o a number...");
					aBuilder.instance(Project, MyProject);
					insert(aConn, aBuilder.model());

					printValidity(aValidator);

					Statement aNumberUntyped = statement(MyProject, number, Values.literal("23"));
					Statement aNumberTypedButOutOfRange = statement(MyProject, number, Values.literal("6000", XMLSchema.INTEGER));
					Statement aNumberTypedAndInRange = statement(MyProject, number, Values.literal("23", XMLSchema.INTEGER));

					System.out.println("Also invalid: number in range, but untyped...");
					insert(aConn, Models2.newModel(aNumberUntyped));
					printValidity(aValidator);

					remove(aConn, Models2.newModel(aNumberUntyped));

					System.out.println("Still invalid: number is typed, but out of range...");
					insert(aConn, Models2.newModel(aNumberTypedButOutOfRange));
					printValidity(aValidator);

					remove(aConn, Models2.newModel(aNumberTypedButOutOfRange));

					System.out.println("Now number is typed, and is in range, this is ok...");
					insert(aConn, Models2.newModel(aNumberTypedAndInRange));
					printValidity(aValidator);

					clear(aValidator);
					aBuilder.reset();
					System.out.println("(7) Employees mustn't work on more than 3 projects");
					System.out.println(Strings.repeat("-", 25) + "\n");

					Constraint aEmployeeWorkOnMaxThreeProjects = ConstraintFactory.constraint(subClassOf(Employee, max(objectProperty(works_on), 3, namedClass(Project))));

					addConstraint(aValidator, aEmployeeWorkOnMaxThreeProjects);

					System.out.println("This is ok since Bob not typed as an Employee...");
					aBuilder.instance(OWL2.NAMED_INDIVIDUAL, Bob);
					insert(aConn, aBuilder.model());

					printValidity(aValidator);

					remove(aConn, aBuilder.model());
					aBuilder.reset();

					System.out.println("This is also ok, Bob only works on one project...");
					aBuilder
						.instance(Employee, Bob)
						.addProperty(works_on, MyProject);

					aBuilder.instance(Project, MyProject);

					insert(aConn, aBuilder.model());

					printValidity(aValidator);

					remove(aConn, aBuilder.model());
					aBuilder.reset();

					System.out.println("But this is not, Bob cannot work on four projects...");
					aBuilder
						.instance(Employee, Bob)
						.addProperty(works_on, MyProject)
						.addProperty(works_on, MyProjectFoo)
						.addProperty(works_on, MyProjectBar)
						.addProperty(works_on, MyProjectBaz);

					aBuilder.instance(Project, MyProject);
					aBuilder.instance(Project, MyProjectFoo);
					aBuilder.instance(Project, MyProjectBar);
					aBuilder.instance(Project, MyProjectBaz);

					insert(aConn, aBuilder.model());

					printValidity(aValidator);

					clear(aValidator);
					aBuilder.reset();
					System.out.println("(8) Departments must have at least 2 employees.");
					System.out.println(Strings.repeat("-", 25) + "\n");

					Constraint aDeptsMustHaveAtLeastTwoEmployees = ConstraintFactory.constraint(subClassOf(Department, min(inverse(works_in), 2, namedClass(Employee))));

					addConstraint(aValidator, aDeptsMustHaveAtLeastTwoEmployees);

					System.out.println("An untyped 'department' is ok...");
					aBuilder.instance(OWL2.NAMED_INDIVIDUAL, MyDepartment);
					insert(aConn, aBuilder.model());

					printValidity(aValidator);

					remove(aConn, aBuilder.model());
					aBuilder.reset();

					System.out.println("This won't be valid, only Bob works in the department, and you need at least two people in a dept...");
					aBuilder.instance(Department, MyDepartment);
					aBuilder
						.instance(Employee, Bob)
						.addProperty(works_in, MyDepartment);

					insert(aConn, aBuilder.model());

					printValidity(aValidator);

					System.out.println("We'll add a second employee to the department, and that will be valid...");
					insert(aConn, Models2.newModel(statement(Alice, RDF.TYPE, Employee), statement(Alice, works_in, MyDepartment)));

					printValidity(aValidator);

					clear(aValidator);
					aBuilder.reset();
					System.out.println("(9) Managers must manage exactly 1 department.");
					System.out.println(Strings.repeat("-", 25) + "\n");

					Constraint aManagerMustManageExactlyOneDepartment = ConstraintFactory.constraint(subClassOf(Manager, cardinality(manages, 1, Department)));

					addConstraint(aValidator, aManagerMustManageExactlyOneDepartment);

					System.out.println("This is ok since the manager is untyped...");
					aBuilder.instance(OWL2.NAMED_INDIVIDUAL, Isabella);
					insert(aConn, aBuilder.model());

					printValidity(aValidator);

					remove(aConn, aBuilder.model());
					aBuilder.reset();

					System.out.println("This is invalid since Isabella is a Manager, but is not managing any departments...");
					aBuilder.instance(Manager, Isabella);
					insert(aConn, aBuilder.model());

					printValidity(aValidator);

					System.out.println("This is valid since now Isabella manages a department...");
					insert(aConn, Models2.newModel(statement(Isabella, manages, MyDepartment),
					                               statement(MyDepartment, RDF.TYPE, Department)));

					printValidity(aValidator);

					System.out.println("This is invalid since now Isabella manages two departments...");
					insert(aConn, Models2.newModel(statement(Isabella, manages, MyDepartment1),
					                               statement(MyDepartment1, RDF.TYPE, Department)));

					printValidity(aValidator);

					clear(aValidator);
					aBuilder.reset();
					System.out.println("(10) Entities must not have more than one name.");
					System.out.println(Strings.repeat("-", 25) + "\n");

					Constraint aEntitesMustHaveOnlyOneName = ConstraintFactory.constraint(functionalProperty(name));

					addConstraint(aValidator, aEntitesMustHaveOnlyOneName);

					System.out.println("Untyped 'department' is ok...");
					aBuilder.instance(OWL2.NAMED_INDIVIDUAL, MyDepartment);
					insert(aConn, aBuilder.model());

					printValidity(aValidator);

					System.out.println("A department with one name is fine too...");
					insert(aConn, Models2.newModel(statement(MyDepartment, name, literal("Human Resources"))));

					printValidity(aValidator);

					System.out.println("But if you add a second name, its invalid...");
					insert(aConn, Models2.newModel(statement(MyDepartment, name, literal("Legal"))));

					printValidity(aValidator);

					clear(aValidator);
					aBuilder.reset();
					System.out.println("(11) The manager of a department must work in that department.");
					System.out.println(Strings.repeat("-", 25) + "\n");

					Constraint aManagerMustWorkInTheirDept = ConstraintFactory.constraint(subPropertyOf(manages, works_in));

					addConstraint(aValidator, aManagerMustWorkInTheirDept);

					System.out.println("Bob manages a department, but does not work in it, this is not ok...");
					insert(aConn, Models2.newModel(statement(Bob, manages, MyDepartment)));

					printValidity(aValidator);

					System.out.println("But if we assert that Bob works in his department, then we're back to being valid...");
					insert(aConn, Models2.newModel(statement(Bob, works_in, MyDepartment)));

					printValidity(aValidator);

					clear(aValidator);
					aBuilder.reset();
					System.out.println("(12) Department managers must supervise all the department's employees.");
					System.out.println(Strings.repeat("-", 25) + "\n");

					Constraint aManagersMustSuperviseAllDeptEmployees = ConstraintFactory.constraint(subPropertyOf(propertyList(objectProperty(manages), inverse(works_in)),
					                                                                                               objectProperty(is_supervisor_of)));

					addConstraint(aValidator, aManagersMustSuperviseAllDeptEmployees);

					aBuilder
						.instance(null /* no type */, Jose)
						.addProperty(manages, MyDepartment)
						.addProperty(is_supervisor_of, Maria);
					aBuilder
						.instance(null /* no type */, Maria)
						.addProperty(works_in, MyDepartment);
					aBuilder
						.instance(null /* no type */, Diego)
						.addProperty(works_in, MyDepartment);

					System.out.println("This data is invalid because Jose is not the supervisor of Diego even though he works in Jose's department...");
					insert(aConn, aBuilder.model());

					printValidity(aValidator);

					System.out.println("But if we assert that Diego is supervised by Jose, the data is again valid");
					insert(aConn, Models2.newModel(statement(Jose, is_supervisor_of, Diego)));

					printValidity(aValidator);

					clear(aValidator);
					aBuilder.reset();
					System.out.println("(13) Complex constraints");
					System.out.println(Strings.repeat("-", 25) + "\n");

					System.out.println("(13a) Employee Constraints: Each employee either works on at least one project, supervises at least\n" +
					                   "one employee that works on at least one project, or manages at least one\n" +
					                   "department.\n");

					Constraint aComplexEmployeeConstraint = ConstraintFactory.constraint(subClassOf(Employee, some(works_on, or(namedClass(Project),
					                                                                                                            some(supervises, and(namedClass(Employee),
					                                                                                                                                 some(works_on, Project))),
					                                                                                                            some(manages, Department)))));

					addConstraint(aValidator, aComplexEmployeeConstraint);

					System.out.println("This is invalid because Esteban is an Employee but does not work on, supervise, or manage anything he's required to...");
					aBuilder.instance(Employee, Esteban);
					insert(aConn, aBuilder.model());

					printValidity(aValidator);

					System.out.println("We've satisfied the violation for Esteban by stating he supervises Lucinda, but we've said nothing about her, so we're still invalid...");
					aBuilder
						.instance(Employee, Esteban)
						.addProperty(supervises, Lucinda);
					aBuilder.instance(Employee, Lucinda);

					insert(aConn, aBuilder.model());

					printValidity(aValidator);

					System.out.println("So now if we state that Lucinda works on a project, we're ok...");
					insert(aConn, Models2.newModel(statement(Lucinda, works_on, MyProject), statement(MyProject, RDF.TYPE, Project)));

					printValidity(aValidator);

					clear(aValidator);
					aBuilder.reset();

					System.out.println("Also valid is saying that Esteban manages a department...");
					aBuilder
						.instance(Employee, Esteban)
						.addProperty(manages, MyDepartment);
					aBuilder.instance(Department, MyDepartment);

					printValidity(aValidator);

					System.out.println("Additionally, we can state that Esteban works on a project and that is ok too...");
					insert(aConn, Models2.newModel(statement(Esteban, works_on, Project),
					                               statement(MyProject, RDF.TYPE, Project)));

					printValidity(aValidator);

					clear(aValidator);
					aBuilder.reset();

					System.out.println("(13b) Employees and US government funding");
					System.out.println("Only employees who are American citizens can work on a project that receives funds from a US government agency.\n");

					Constraint aOnlyCitizensWorkForGovtFundedProject = ConstraintFactory.constraint(subClassOf(and(namedClass(Project), some(receives_funds_from, US_Government_Agency)),
					                                                                                           all(inverse(works_on), and(namedClass(Employee), hasValue(nationality, literal("US"))))));

					addConstraint(aValidator, aOnlyCitizensWorkForGovtFundedProject);

					System.out.println("This is ok, we havent made any statements about people working on the project, so this is valid...");
					aBuilder
						.instance(Project, MyProject)
						.addProperty(receives_funds_from, NASA);
					aBuilder
						.instance(US_Government_Agency, NASA);

					insert(aConn, aBuilder.model());
					printValidity(aValidator);

					System.out.println("But now this will be invalid because we've stated that Andy works on the project, but we've not stated that he's a US citizen...");

					insert(aConn, Models2.newModel(statement(Andy, RDF.TYPE, Employee),
					                               statement(Andy, works_on, MyProject)));

					printValidity(aValidator);


					System.out.println("We can fix that by stating that he's a citizen...");

					insert(aConn, Models2.newModel(statement(Andy, nationality, literal("US"))));

					printValidity(aValidator);

					System.out.println("This is invalid; even though we've stated Heidi's nationality correctly, she's a Supervisor rather than an Employee...");
					insert(aConn, Models2.newModel(statement(Heidi, RDF.TYPE, Supervisor),
					                               statement(Heidi, works_on, MyProject),
					                               statement(Heidi, nationality, literal("US"))));

					printValidity(aValidator);

					System.out.println("Now if we state the subclass relationship between Employee and Supervisor, we're valid again...");
					insert(aConn, Models2.newModel(statement(Supervisor, RDFS.SUBCLASSOF, Employee)));
					printValidity(aValidator);
				}
				finally {
					if (dbms.list().contains("testICVDocs")) {
						dbms.drop("testICVDocs");
					}
				}
			}
		}
		finally {
			aStardog.shutdown();
		}
	}

	private static void addConstraint(final ICVConnection theValidator, final Constraint... theConstraint) throws StardogException {
		theValidator.addConstraint(theConstraint);
	}

	private static Value literal(final String theValue) {
		return Values.literal(theValue);
	}

	private static Statement statement(final IRI theSubj, final IRI thePred, final Value theObject) {
		return Values.statement(theSubj, thePred, theObject);
	}

	private static void printValidity(final ICVConnection theValidator) throws StardogException {
		final boolean isValid = theValidator.isValid(ContextSets.DEFAULT_ONLY);
		System.out.println("The data " + (isValid
		                                  ? "is"
		                                  : "is NOT") + " valid!");

		if (!isValid) {
			try (CloseableIterator<ConstraintViolation<BindingSet>> aViolationIter = theValidator.getViolationBindings(ContextSets.DEFAULT_ONLY)) {

				while (aViolationIter.hasNext()) {
					ConstraintViolation<BindingSet> aViolation = aViolationIter.next();

					// ICV.asIndividuals will close the `aViolation.getViolations()` for us
					Iterator<Resource> aViolatingIndividuals = ICV.asIndividuals(aViolation.getViolations());

					System.out.println("Each of these individuals violated the constraint: " + aViolation.getConstraint());

					while (aViolatingIndividuals.hasNext()) {
						System.out.println(aViolatingIndividuals.next());
					}
				}
			}
		}
		System.out.println();
	}

	private static void insert(final Connection theConn, final Model theGraph) throws StardogException {
		theConn.begin();
		theConn.add().graph(theGraph);
		theConn.commit();
	}

	private static void remove(final Connection theConn, final Model theGraph) throws StardogException {
		theConn.begin();
		theConn.remove().graph(theGraph);
		theConn.commit();
	}

	private static void clear(final ICVConnection theConn) throws StardogException {
		theConn.begin();
		theConn.remove().all();
		theConn.clearConstraints();
		theConn.commit();
	}
}
