import os

from case_library.case.case import Case
from case_library.case.problem import Problem
from case_library.xml.XmlManager import XmlManager
from datasets.utils.file_manager import file_read, read_alcoholic_percentage_file, file_write

COCKTAIL_RECIPE_LIB = "/../datasets/cocktail_recipe_lib"

COCKTAIL_RECIPE_LIB_ALCOHOL = "/../datasets/cocktail_recipe_lib_alcohol"


class CaseLibrary:
    def __init__(self, includeAlcoholicPercentages=False):
        self.includeAlcoholicPercentages = includeAlcoholicPercentages
        self.folder_path = os.path.realpath(__file__)[0:os.path.realpath(__file__).rfind("/")]
        if includeAlcoholicPercentages:
            self.problems = file_read(self.folder_path + COCKTAIL_RECIPE_LIB_ALCOHOL)
        else:
            self.problems = file_read(self.folder_path + COCKTAIL_RECIPE_LIB)
        self.alcoholicLiqueurs = self.read_similarities(file_read(self.folder_path + "/../datasets/alcoholicLiqueurs"))
        self.nonalcoholicLiqueurs = self.read_similarities(
            file_read(self.folder_path + "/../datasets/nonalcoholicLiqueurs"))
        self.fruit = self.read_similarities(file_read(self.folder_path + "/../datasets/fruit"))
        self.vegetables = self.read_similarities(file_read(self.folder_path + "/../datasets/vegetables"))
        self.tasteEnhancers = self.read_similarities(file_read(self.folder_path + "/../datasets/tasteEnhancers"))
        self.other = self.read_similarities(file_read(self.folder_path + "/../datasets/others"))
        self.cocktail_dict = XmlManager().getCocktailDict(self.folder_path, self.alcoholicLiqueurs,
                                                          self.nonalcoholicLiqueurs,
                                                          self.fruit, self.vegetables,self.other, self.tasteEnhancers,
                                                          should_include_alcoholic_percentages=includeAlcoholicPercentages)
        


        self.categories = ['fruit', 'vegetables', 'alcoholicLiqueurs', 'nonalcoholicLiqueurs', 'tasteEnhancers', 'other']
        self.sims = {}
        self.sims['fruit'] = self.fruit
        self.sims['vegetables'] = self.vegetables
        self.sims['alcoholicLiqueurs'] = self.alcoholicLiqueurs
        self.sims['nonalcoholicLiqueurs'] = self.nonalcoholicLiqueurs
        self.sims['tasteEnhancers'] = self.tasteEnhancers
        self.sims['other'] = self.other

        self.cases = self.initializeCases()

    def getCocktailRecipe(self, cocktail_name):
        return self.cocktail_dict[cocktail_name]

    def read_similarities(self, content):
        previous_name = ""
        res = dict()
        for line in content:
            split = line.split(",")
            if previous_name != split[0]:
                res[split[0].lower()] = dict()
                previous_name = split[0]
            res[split[0].lower()][split[1].lower()] = float(split[2].replace("\n",""))
        return res

    def initializeCases(self):
        cases = list()
        for problem in self.problems:
            cocktail_name = problem.split(",")[0].strip()
            problem_tmp = Problem(problem, self)
            temp_case = Case(problem_tmp, self.cocktail_dict[cocktail_name])
            cases.append(temp_case)
        return cases

    def get_ingredient_type(self, food_name):
        return XmlManager.get_ingredient_type(self.alcoholicLiqueurs, food_name, self.fruit, self.nonalcoholicLiqueurs, self.tasteEnhancers,
                                       self.vegetables, self.other)
    def add_new_case(self, case):
        self.cases.append(case)
        self.store_new_case_in_file(case)

    def store_new_case_in_file(self, case):
        case_xml = self.xml_encode(case, self.includeAlcoholicPercentages)
        print(case_xml)
        self.store_new_cocktail_xml(case_xml)
        self.store_new_problem(case)

    def set_no_times_per_solution(self,cocktail,no_times_used,no_times_accepted):
        xml_content = file_read(
            self.folder_path + ("/../datasets/cocktailsAlcoholicPercentage.xml" if self.includeAlcoholicPercentages
                                else "/../datasets/cocktails.xml"))
        cocktail_index = 0
        for i, line in enumerate(xml_content):
            if "<title>"+cocktail.name+"</title>" in line:
                cocktail_index = i
        xml_content[cocktail_index+1] = "   <no_used>"+str(no_times_used)+"</no_used>\n"
        xml_content[cocktail_index+2] = "   <no_times_accepted>"+str(no_times_accepted)+"</no_times_accepted>\n"
        file_write(
            self.folder_path + ("/../datasets/cocktailsAlcoholicPercentage.xml" if self.includeAlcoholicPercentages
                                else "/../datasets/cocktails.xml"), xml_content)
        self.cocktail_dict.get(cocktail.name).set_no_used(no_times_used)
        self.cocktail_dict.get(cocktail.name).set_no_times_accepted(no_times_accepted)

    def store_new_cocktail_xml(self, case_xml):
        xml_content = file_read(
            self.folder_path + ("/../datasets/cocktailsAlcoholicPercentage.xml" if self.includeAlcoholicPercentages
                                else "/../datasets/cocktails.xml"))
        xml_content[-1] = case_xml
        xml_content.append("</recipes>")
        file_write(
            self.folder_path + ("/../datasets/cocktailsAlcoholicPercentage.xml" if self.includeAlcoholicPercentages
                                else "/../datasets/cocktails.xml"), xml_content)

    def xml_encode(self, case, shouldIncludeAlcohol):
        xml_string = "<recipe>\n"
        xml_string += "<title>" + case.solution.name + "</title>\n"
        xml_string += "<no_used>0</no_used>\n<no_times_accepted>0</no_times_accepted>\n"
        if shouldIncludeAlcohol:
            xml_string += "<alcoholicPercentage>" + (str(case.solution.alcoholic_percentage) if case.solution.alcoholic_percentage != -1 else str(
                self.calculate_alcohol_percentage(case.solution))) + "</alcoholicPercentage>\n"
        xml_string += "<ingredients>\n"
        for ingredient in case.solution.ingredients:
            if not hasattr(ingredient,'name'):
                continue
            xml_string += "<ingredient"
            xml_string += " food=\"" + case.solution.ingredients[ingredient].name + "\""
            xml_string += " quantity=\"" + str(case.solution.ingredients[ingredient].quantity) + "\""
            xml_string += " unit=\"" + case.solution.ingredients[ingredient].unit + "\">"
            xml_string += str(case.solution.ingredients[ingredient].quantity) + " " + case.solution.ingredients[
                ingredient].unit + " " + case.solution.ingredients[ingredient].name
            xml_string += "</ingredient>\n"
        xml_string += "</ingredients>\n"
        xml_string += "<preparation>\n"
        for step in case.solution.steps:
            xml_string += "<step>" + step + "</step>\n"
        xml_string += "</preparation>\n"
        xml_string += "</recipe>\n"
        return xml_string

    def xml_encode_cocktail(self, cocktail, shouldIncludeAlcohol):
        xml_string = "<recipe>\n"
        xml_string += "<title>" + cocktail.name + "</title>\n"
        xml_string += "<no_used>0</no_used>\n<no_times_accepted>0</no_times_accepted>\n"
        if shouldIncludeAlcohol:
            xml_string += "<alcoholicPercentage>" + (str(cocktail.alcoholic_percentage) if cocktail.alcoholic_percentage != -1 else str(
                self.calculate_alcohol_percentage(cocktail))) + "</alcoholicPercentage>\n"
        xml_string += "<ingredients>\n"
        for ingredient in cocktail.ingredients:
            if not hasattr(ingredient,'name'):
                continue
            xml_string += "<ingredient"
            xml_string += " food=\"" + cocktail.ingredients[ingredient].name + "\""
            xml_string += " quantity=\"" + str(cocktail.ingredients[ingredient].quantity) + "\""
            xml_string += " unit=\"" + cocktail.ingredients[ingredient].unit + "\">"
            xml_string += str(cocktail.ingredients[ingredient].quantity) + " " + cocktail.ingredients[
                ingredient].unit + " " + cocktail.ingredients[ingredient].name
            xml_string += "</ingredient>\n"
        xml_string += "</ingredients>\n"
        xml_string += "<preparation>\n"
        for step in cocktail.steps:
            xml_string += "<step>" + step + "</step>\n"
        xml_string += "</preparation>\n"
        xml_string += "</recipe>\n"
        return xml_string

    def calculate_alcohol_percentage(self, cocktail_recipe):
        alcohol_percentages = read_alcoholic_percentage_file()
        total_amount = 0
        alcoholic_percentages = []
        for ingredient in cocktail_recipe.ingredients:
            temp = cocktail_recipe.ingredients[ingredient]
            if temp.unit == "cl" or temp.unit == "l":
                if temp.unit == "cl":
                    multiplier = 10
                else:
                    multiplier = 1000
                quantity_in_ml = float(temp.quantity) * multiplier
                total_amount += quantity_in_ml
                if temp.name in alcohol_percentages.keys():
                    alcoholic_percentages.append([quantity_in_ml, alcohol_percentages[temp.name]])
                else:
                    alcoholic_percentages.append([quantity_in_ml, 0])

        # TODO double check for correctness
        cocktail_alcohol_percentage = 0
        for item in alcoholic_percentages:
            cocktail_alcohol_percentage += (item[0] / total_amount) * item[1]
        return cocktail_alcohol_percentage

    def store_new_problem(self, case):
        problem_string = case.solution.name + ","
        for key in case.problem.problem_definition:
            if key == "alcoholic_category":  # since the other problems have the alcoholicPercentage as int,
                # we gonna take the alcoholic percentage from the solution
                problem_string += "alcoholicPercentage:" + str(
                    int(self.calculate_alcohol_percentage(case.solution))) + ","
            else:
                arr = case.problem.problem_definition[key]
                for item in arr:
                    problem_string += key+":"+item+","
        problem_string = problem_string[0:-1] # removes the "," at the end
        print(problem_string)
        self.store_problem_string(problem_string)

    def store_problem_string(self, problem_string):
        problems_content = file_read(
            self.folder_path + (COCKTAIL_RECIPE_LIB_ALCOHOL if self.includeAlcoholicPercentages
                                else COCKTAIL_RECIPE_LIB))
        problems_content.append(problem_string)
        file_write(
            self.folder_path + (COCKTAIL_RECIPE_LIB_ALCOHOL if self.includeAlcoholicPercentages
                                else COCKTAIL_RECIPE_LIB), problems_content)
# TODO Remove just to test
# cl = CaseLibrary(True)
# cl.set_no_times_per_solution(cl.cocktail_dict["Exotic Cocktail passion fruit"],1,2)