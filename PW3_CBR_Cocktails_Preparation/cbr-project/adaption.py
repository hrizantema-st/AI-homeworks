from case_library.CaseLibrary import CaseLibrary
from retrieval import distance, get_closest
import retrieval
from case_library.case.problem import Problem
class adaption():
   def __init__(self,problem, solution,cl,isAdaptable=True):
       self.cl=cl
       self.porblem = problem
       self.solution = solution
       self.isAdaptable = isAdaptable
   def adapt(self):
       #TODO strategy: structural adaptation
       '''
       Parameters:
           problem: a problem
           solution: a solution
       Returns:
           solution, adapted according to the problem
       '''

       adaptedsolution=self.solution
       if self.isAdaptable == True:
           # step 1 find if there are difference between p and s
           problemdic = self.porblem.problem_definition
           all_elementProblem=[]
           different_item = {}
           invers_dic = {} # this dic used for finding the key by value
           for key in problemdic:
               if problemdic[key]!='None' and key != "alcoholicPercentage":
                   for element in problemdic[key]:
                       all_elementProblem.append(element)
                       invers_dic[element]=key
           different_set = set(all_elementProblem)-set(self.solution.ingredients.keys())
           for itme in different_set:
               different_item[invers_dic[itme]] = itme
           if len(different_item) != 0:
               pairs = []
               HasGoodNewSolution = False
               if HasGoodNewSolution == False:
                   for itme in different_set:
                       key=invers_dic[itme]
                       pair = ':'.join([key,itme])
                       pairs.append(pair)
                   NewProblme_str=','.join(pairs)
                   new_problme=Problem(NewProblme_str) # obtain the new problem
                   d, s, c2 = get_closest(new_problme, self.cl)
                   s_temp = c2.solution

                   ingredients_temp=s_temp.ingredients.copy()
                   if len(different_set)==len(ingredients_temp):
                       adaptedsolution.ingredients.update(ingredients_temp)# merge
                   else:
                       for itme in different_set:
                           adaptedsolution.ingredients[itme] = invers_dic[itme]
       else:
           pass# do nothing for the time being
       return adaptedsolution
# example of how to use adaption:
# clAll = CaseLibrary(True)
# c1 = clAll.cases[1]
# p1 = c1.problem
# s1 = c1.solution
#
#
# # add some spacial_ingredient for testing like
# p1.problem_definition["fruit"]=['blood orange']
# p1.problem_definition["alcoholicPercentage"]=['MEDI']
#
# #p1.problem_definition["alcoholicLiqueurs"] = ['cognac']
#
# adaptor = adaption(p1,s1,clAll)
# adaptedsolution = adaptor.adapt()

