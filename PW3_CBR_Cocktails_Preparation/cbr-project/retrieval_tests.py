from case_library.CaseLibrary import CaseLibrary
from case_library.case.problem import Problem
from retrieval import distance, get_closest

import numpy as np


print('Loading case library...')
cl = CaseLibrary(True)
print('Loaded case library with ' + str(len(cl.cases)) + ' cases')
print('- ' * 25)

print('Test ingredient similarities:')
violations = []
for cat in cl.categories:
	ings = list(cl.sims[cat].keys())
	print(ings)
	for ing in ings:
		if cl.sims[cat][ing][ing] != 1:
			violations.append(ing)
	print(violations)


print('- ' * 25)
print('Min & Max distance?')
print('min should be 0, max should be 1')


dists = []
violations = []
for i, c1 in enumerate(cl.cases):
	for j, c2 in enumerate(cl.cases):
		d, _ = distance(c1.problem, c2.problem, cl)
		dists.append(d)
		if d < 0 or d > 1:
			violations.append((i, j))
print(min(dists))
print(max(dists))
print(min(dists) == 0 and max(dists) == 1)
print(violations)


print('- ' * 25)

print('Symmetry?')
print('d(a,b) == d(b, a)')
all_symmetric = True
violations = []
for i, c1 in enumerate(cl.cases):
	for j, c2 in enumerate(cl.cases):
		da, _ = distance(c1.problem, c2.problem, cl)
		db, _ = distance(c2.problem, c1.problem, cl)
		if da != db:
			all_symmetric = False
			violations.add((i, j))
print(all_symmetric)
print(violations)

print('- ' * 25)
print('Reflexivity?')
print('d(a, a) == 0')
all_reflexive = True
violations = []
for i, c1 in enumerate(cl.cases):
	d, _ = distance(c1.problem, c1.problem, cl) 
	if d != 0:
		all_reflexive = False
		violations.append(i)
print(all_reflexive)
print(violations)
print('- ' * 25)



# p1 = Problem("None,alcoholicLiqueurs:rum,fruit:lime,fruit:orange,tasteEnhancers:sugar,other:ice cube,alcoholicPercentage:5,nonalcoholicLiqueurs:syrup,alcoholicPercentage:Low", cl)

p1 = cl.cases[32].problem
print('problem1:')
for attribute in p1.problem_definition.keys():
    print('\t' + attribute + ': ' + str(p1.problem_definition[attribute]))


print(distance(p1, p1, cl))

# print(get_closest(p1, cl))
# _, _, c2 = get_closest(p1, cl)
# print('- ' * 30)
# p2 = c2.problem
# print('found problem:')
# print(p2)
# for attribute in p2.problem_definition.keys():
#     print('\t' + attribute + ': ' + str(p2.problem_definition[attribute]))
# s2 = c2.solution
# print('found solution:')
# print('\tname: ' + str(s2.name))
# print('\tingredients: ')
# for ingred in s2.ingredients:
#     print('\t\t' + ingred)
# print('\tsteps:')
# for step in s2.steps:
#     print('\t\t' + step)
# print('\talcoholic percentage: ' + str(s2.alcoholic_percentage))


