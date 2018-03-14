from case_library.case.cocktail import Cocktail

class Problem:
    def __init__(self,problem_string, caseLibrary=None):
        parameters = problem_string.split(",")
        self.problem_definition = dict()
        if caseLibrary != None:
            for constraint in parameters:
                if ":" in constraint:
                    constraint = constraint.split(":")
                    if constraint[0] == "alcoholicPercentage":
                        alcohol_content = constraint[1]
                        self.problem_definition[constraint[0]] = alcohol_content.replace("\n","")
                        self.alcoholFree = (alcohol_content == "None")
                    else:
                        food_name = constraint[1].replace("\n", "").lower()
                        ingredient_type = caseLibrary.get_ingredient_type(food_name)
                        if ingredient_type not in self.problem_definition:
                            self.problem_definition[ingredient_type] = []
                        self.problem_definition[ingredient_type].append(food_name)
                else: # if there is no ":" in the string, it means it is just the cocktail name
                    self.cocktail_name = constraint
