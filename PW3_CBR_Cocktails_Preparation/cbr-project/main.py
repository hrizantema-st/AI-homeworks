from case_library.CaseLibrary import CaseLibrary
from case_library.case.case import Case
from datasets.utils.file_manager import write_proposal_to_file
from retrieval import distance, get_closest
from case_library.case.problem import Problem
from adaption import adaption

choices = {}

print('Loading case library...')
cl = CaseLibrary(True)
print('Loaded case library with ' + str(len(cl.cases)) + ' cases')
print('- ' * 20)


print('Welcome to our cocktail recommender system!')
print('We will ask you for some of the ingredients you\'d like in your drink now.')
print('How strong do you want your cocktail to be?')
print('Options: None, Low, Medium, Strong')
categories = ['None', 'Low', 'Medium', 'Strong']
while True:
    usr_in = input('> ')
    usr_in = usr_in.lower().title()
    if usr_in in categories:
        break
    else:
        print('Invalid input, please try again.')

print('You have chosen: ' + usr_in)
choices['alc_cat'] = usr_in
print('- ' * 20)

if choices['alc_cat'] != 'None':
    print('It looks like you would like some alcohol in your drink. Which ones do you like?')
    print('The options are:')
    print(list(cl.sims['alcoholicLiqueurs'].keys()))
    alcs_lc = []
    alcs = []
    for alc in list(cl.sims['alcoholicLiqueurs'].keys()):
        alcs.append(alc)
        alcs_lc.append(alc.lower())

    print('\nAdd as many as you want. Type "X" when you\'re done')
    chosen_alcs = []
    while True:
        usr_in = input('> ')
        if usr_in.lower() in alcs_lc:
            usr_in = alcs[alcs_lc.index(usr_in.lower())]
            if usr_in not in chosen_alcs:
                print('Added to your list of alcholols. Type "X" when you\'re done.')
                chosen_alcs.append(usr_in)
            else:
                print('You have already selected this.')
        elif usr_in.lower() == 'x':
            if False: #if len(chosen_alcs) == 0:
                print('Are you sure you don\'t want to select anything? Type "yes" to confirm. Type "no" to go back.')
                usr_in = input('> ')
                if usr_in.lower() == 'yes':
                    break
                else:
                    print("Okay, let's continue! Pick an ingredient:")
            else:
                break
        else:
            print('Invalid input, please try again.')
    print('You have chosen:')
    print(chosen_alcs)
    print('- ' * 20)
else:

    chosen_alcs = []
choices['alc'] = chosen_alcs



ingr_cats = ['non-alcoholic liquids', 'fruits', 'plant based aromas', 'taste enhancers', 'ice or water']
ingr_cat_names = ['nonalcoholicLiqueurs', 'fruit', 'vegetables', 'tasteEnhancers', 'other']

for i in range(len(ingr_cats)):
    ingr_cat = ingr_cats[i]
    ingr_cat_name = ingr_cat_names[i]
    print('Now, would you like any ' + ingr_cat + '?')
    print('The options are:')
    print(list(cl.sims[ingr_cat_name].keys()))

    ingrs_lc = []
    ingrs = []
    for ingr in list(cl.sims[ingr_cat_name].keys()):
        ingrs.append(ingr)
        ingrs_lc.append(ingr.lower())

    print('\nAdd as many as you want. Type "X" when you\'re done')
    chosen_ingrs = []
    while True:
        usr_in = input('> ')
        if usr_in.lower() in ingrs_lc:
            usr_in = ingrs[ingrs_lc.index(usr_in.lower())]
            if usr_in not in chosen_ingrs:
                print('Added to your list. Type "X" when you\'re done.')
                chosen_ingrs.append(usr_in)
            else:
                print('You have already selected this.')
        elif usr_in.lower() == 'x':
            if False: #if len(chosen_ingrs) == 0:
                print('Are you sure you don\'t want to select anything? Type "yes" to confirm. Type "no" to go back.')
                usr_in = input('> ')
                if usr_in.lower() == 'yes':
                    break
                else:
                    print("Okay, let's continue! Pick an ingredient:")
            else:
                break
        else:
            print('Invalid input, please try again.')
    print('You have chosen:')
    print(chosen_ingrs)
    choices[ingr_cat] = chosen_ingrs
    print('- ' * 20)


print('Great! We have selected all your favorite ingredients.')
print('Now the system will look for a fitting recipe. This might take a while, stay with us!')
print('- ' * 20)

problem_string = "alcoholicPercentage:" + choices['alc_cat']
for alc in choices['alc']:
    problem_string += ',alcoholicLiqueurs:' + alc
for i in range(len(ingr_cats)):
    ingr_cat = ingr_cats[i]
    ingr_cat_name = ingr_cat_names[i]
    for ingr in choices[ingr_cat]:
        problem_string += ',' + ingr_cat_name + ':' + ingr
problem = Problem(problem_string, cl)
distance, matched_feats, sol_case = get_closest(problem, cl)
print('- ' * 20)
print('You have asked for the following:')
for attribute in problem.problem_definition.keys():
    print('\t' + attribute + ': ' + str(problem.problem_definition[attribute]))
print('- ' * 20)
print('We found a case with a distance of ' + str(distance) + ' and ' +str(matched_feats) + ' matched ingredient categories')
print('The best matching case in the case base is:')
p2 = sol_case.problem
print('Problem:')
for attribute in p2.problem_definition.keys():
    print('\t' + attribute + ': ' + str(p2.problem_definition[attribute]))
s2 = sol_case.solution
print('Solution:')
print('\tname: ' + str(s2.name))
print('\tingredients: ')
for ingred in s2.ingredients:
    print('\t\t' + ingred)
print('\tsteps:')
for step in s2.steps:
    print('\t\t' + step)
print('\talcoholic percentage: ' + str(s2.alcoholic_percentage))
print('- ' * 20)

print('This case was found as best match ' + str(sol_case.solution.get_no_used()) + ' times')

print('We will now adapt the found solution to fit better to your problem.')
print('One second...')
print('- ' * 20)

adaptor = adaption(problem,s2,cl)
s3 = adaptor.adapt()


print('The adapted solution is:')
print('\tname: ' + str(s3.name) + ' (adapted)')
print('\tingredients: ')
for ingred in s3.ingredients:
    print('\t\t' + ingred)
print('\tsteps:')
for step in s3.steps:
    print('\t\t' + step)
print('\talcoholic percentage: ' + str(s3.alcoholic_percentage))
print('- ' * 20)


print('EVALUATION:')
print('Please give us some quick feedback how you like the proposed cocktail')
print('Is the solution useful? Type "yes" or "no"')
usr_useful = input('> ')
print('Did the adaption further improve the proposed cocktail? Type "yes" or "no"')
usr_adaption_useful = input('> ')
print('Do you have a proposal of how to improve or repair the proposed solution? Type anything to our cocktail experts:')
usr_repair_proposal = input('> ')

print('- ' * 20)
print('Thanks for your feedback! We will use this to make future proposals even better.')
increment_useful = 0
if usr_useful.lower() == 'yes':
    increment_useful = 1
    if usr_adaption_useful.lower() == 'yes':
        cl.store_new_case_in_file(Case(problem, s3))
        print('A new case has been added to the case library.')
# Incrementing counters
cl.set_no_times_per_solution(sol_case.solution,sol_case.solution.get_no_used() + 1,
                             sol_case.solution.get_no_times_accepted() + increment_useful)
# Writing proposal to file
if len(usr_repair_proposal) >= 2 :
    write_proposal_to_file(cl, usr_repair_proposal,problem_string,s3,True)

print('- ' * 20)
print('Now ... enjoy your drink!')





        



