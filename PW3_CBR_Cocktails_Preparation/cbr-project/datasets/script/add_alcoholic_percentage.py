import os
from random import random
from random import choice

from case_library.CaseLibrary import CaseLibrary
from case_library.case.cocktail import Cocktail
from datasets.utils.file_manager import file_read
from datasets.utils.file_manager import file_write


def read_percentages(content):
    res = dict()
    for line in content:
        split = line.split(",")
        res[split[0]] = float(str.strip(split[1]))
    return res


def read_alcoholic_percentage_file():
    folder_path = os.path.realpath(__file__)[0:os.path.realpath(__file__).rfind("/")]
    alcoholic_percentages_content = file_read(folder_path + "/../alcoholic_percentages")
    alcoholic_percentages = read_percentages(alcoholic_percentages_content)
    return alcoholic_percentages


alcohol_percentages = read_alcoholic_percentage_file()

caseLibrary = CaseLibrary()


def calculate_alcohol_percentage(cocktail_recipe):
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


content_modified_alcohol_lib = []

folder_path = os.path.realpath(__file__)[0:os.path.realpath(__file__).rfind("/")]
f = open(folder_path + "/../cocktails.xml")
content = f.readlines()
f.close()

percentages_dict = {}

for line in content:
    content_modified_alcohol_lib.append(line)
    if "<title>" in line:
        cocktail_name = line.replace("<title>", "").replace("</title>", "").strip()
        cocktail_recipe = caseLibrary.getCocktailRecipe(cocktail_name)
        percentage = calculate_alcohol_percentage(cocktail_recipe)
        content_modified_alcohol_lib.append(
            "    <alcoholicPercentage>%f</alcoholicPercentage>\n" % percentage)
        percentages_dict[cocktail_name] = percentage

# print(content_modified)
# file_write(folder_path + "/../cocktailsAlcoholicPercentage.xml", content_modified)
#
f = open(folder_path + "/../cocktail_recipe_lib")
content = f.readlines()
f.close()

content_modified_alcohol_lib = []
content_modified = []

for line in content:
    cocktail_name = line.split(",")[0]
    toAdd = line
    solution = caseLibrary.cocktail_dict[cocktail_name.strip()]
    possible_ingredients = []
    for ingredient in solution.ingredients:
        if solution.ingredients[ingredient].name not in toAdd:
            possible_ingredients.append([solution.ingredients[ingredient].name,solution.ingredients[ingredient].type])
    if len(possible_ingredients) > 0:
        random_i = choice(possible_ingredients)
        toAdd = toAdd.replace("\n", "") + (",%s:%s\n" % (random_i[1],random_i[0]))
    content_modified.append(toAdd)
    if random() >= 0.8:
        toAdd = toAdd.replace("\n", "") + (
        ",alcoholicPercentage:%s\n" % Cocktail.get_alcoholic_percentage_category(percentages_dict[cocktail_name.strip()]))
    content_modified_alcohol_lib.append(toAdd)

file_write(folder_path + "/../cocktail_recipe_lib_alcohol", content_modified_alcohol_lib)
file_write(folder_path + "/../cocktail_recipe_lib", content_modified)