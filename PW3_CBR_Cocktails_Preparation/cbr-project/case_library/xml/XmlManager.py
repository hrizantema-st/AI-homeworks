from xml.dom import minidom

from case_library.case.cocktail import Cocktail
from case_library.case.ingredient import Ingredient


class XmlManager:

    def getCocktailDict(self, folder_path, alcoholicLiqueurs, nonalcoholicLiqueurs, fruit, vegetables, other,
                        tasteEnhancers, should_include_alcoholic_percentages):
        if should_include_alcoholic_percentages:
            xmldoc = minidom.parse(folder_path + '/../datasets/cocktailsAlcoholicPercentage.xml')
        else:
            xmldoc = minidom.parse(folder_path + '/../datasets/cocktails.xml')
        cocktaillist = xmldoc.getElementsByTagName('title')
        cocktail_dict = dict()
        for cocktail in cocktaillist:
            ingredients = cocktail.parentNode.getElementsByTagName('ingredient')
            ingredients_list = dict ()
            temp_cocktail = Cocktail(cocktail.firstChild.nodeValue)
            for ingredient in ingredients:
                food_name = ingredient.attributes["food"].value.lower()
                type = self.get_ingredient_type(alcoholicLiqueurs, food_name, fruit, nonalcoholicLiqueurs, tasteEnhancers,
                                                vegetables, other)
                quantity = ingredient.attributes["quantity"].value
                unit = ingredient.attributes["unit"].value
                ingredients_list[food_name] = (Ingredient(food_name,type, unit, quantity))

            steps = cocktail.parentNode.getElementsByTagName('step')
            step_list = []
            for step in steps:
                step_list.append(step.firstChild.nodeValue)
            temp_cocktail.ingredients = ingredients_list
            temp_cocktail.steps = step_list
            if should_include_alcoholic_percentages:
                alcohol = cocktail.parentNode.getElementsByTagName('alcoholicPercentage')
                temp_cocktail.set_alcoholic_percentage(float(alcohol[0].firstChild.nodeValue))
            no_used = cocktail.parentNode.getElementsByTagName('no_used')
            temp_cocktail.set_no_used(int(no_used[0].firstChild.nodeValue))
            no_times_accepted = cocktail.parentNode.getElementsByTagName('no_times_accepted')
            temp_cocktail.set_no_times_accepted(int(no_times_accepted[0].firstChild.nodeValue))
            cocktail_dict[temp_cocktail.name] = temp_cocktail
        return cocktail_dict

    @staticmethod
    def get_ingredient_type(alcoholicLiqueurs, food_name, fruit, nonalcoholicLiqueurs, tasteEnhancers,
                            vegetables, other):
        if food_name in alcoholicLiqueurs:
            type = "alcoholicLiqueurs"
        elif food_name in nonalcoholicLiqueurs:
            type = "nonalcoholicLiqueurs"
        elif food_name in fruit:
            type = "fruit"
        elif food_name in vegetables:
            type = "vegetables"
        elif food_name in tasteEnhancers:
            type = "tasteEnhancers"
        elif food_name in other:
            type = "other"
        else:
            type = "unknown"
        return type