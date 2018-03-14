# This class will represent a cocktail, since the problem is to create cocktails
# Each case will have
# #  The ingredients, categorized by type
# #  The recipe steps


class Cocktail:

    def __init__(self, name, ingredients = None, steps = None, alcoholic_percentage = None):
        self.no_times_accepted = 0
        self.no_times_used = 0
        self.steps = steps
        self.ingredients = ingredients
        self.name = name
        self.alcoholic_percentage = alcoholic_percentage
        if alcoholic_percentage is not None:
            self.alcoholic_category = self.get_alcoholic_percentage_category(alcoholic_percentage)

    @staticmethod
    def get_alcoholic_percentage_category(alcoholic_percentage):
        if alcoholic_percentage == 0:
            return "None"
        elif alcoholic_percentage <= 15:
            return "Low"
        elif alcoholic_percentage > 15 and alcoholic_percentage <= 20:
            return "Medium"
        else:
            return "Strong"

    def set_alcoholic_percentage(self, alcoholic_percentage):
        self.alcoholic_percentage = alcoholic_percentage
        if alcoholic_percentage is not None:
            self.alcoholic_category = self.get_alcoholic_percentage_category(alcoholic_percentage)

    def set_no_times_accepted(self, no):
        self.no_times_accepted = no

    def set_no_used(self, no):
        self.no_times_accepted = no

    def get_no_times_accepted(self):
        return self.no_times_accepted

    def get_no_used(self):
        return self.no_times_used
