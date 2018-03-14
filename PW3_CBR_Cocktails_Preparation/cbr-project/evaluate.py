from case_library.CaseLibrary import CaseLibrary
from retrieval import distance, get_closest
from case_library.case.problem import Problem
from adaption import adaption


print('Loading case library...')
cl = CaseLibrary(True)
print('Loaded case library with ' + str(len(cl.cases)) + ' cases')
print('- ' * 20)


problems = []
with open('datasets/test_set/problems.txt', 'r') as prob_file:
	for line in prob_file:
		problems.append(Problem(line, cl))

solutions = []
with open('datasets/test_set/targets.txt', 'r') as sol_file:
	for line in sol_file:
		solutions.append(line.strip())


correct = 0
for i, problem in enumerate(problems):
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
	if solutions[i].lower().strip() == s2.name.lower().strip():
		correct += 1
	print('\tingredients: ')
	for ingred in s2.ingredients:
	    print('\t\t' + ingred)
	print('\tsteps:')
	for step in s2.steps:
	    print('\t\t' + step)
	print('\talcoholic percentage: ' + str(s2.alcoholic_percentage))
	print('- ' * 20)

print('CORRECT: ' + str(correct) + '/' + str(len(solutions)))
