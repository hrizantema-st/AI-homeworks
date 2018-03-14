from case_library.CaseLibrary import CaseLibrary


ALC_CAT_WEIGHT = 10

# ['fruit', 'vegetables', 'alcoholicLiqueurs', 'nonalcoholicLiqueurs', 'tasteEnhancers', 'other']
INGR_CAT_WEIGHTS = [2, 1, 5, 5, 2, 1]

def most_similar(element, list_of_elements, all_similarities):
    max_similarity = 0
    if element in all_similarities:
        for x_fruit in list_of_elements:
            if x_fruit in all_similarities[element]:
                if all_similarities[element][x_fruit] > max_similarity:
                    max_similarity = all_similarities[element][x_fruit]
    return max_similarity


def calculate_sijk(p1, p2, attr_type, all_similarities):
    x_list = p1.problem_definition[attr_type]
    y_list = p2.problem_definition[attr_type]

    sim_x = 0
    for element in x_list:
        sim_x += most_similar(element, y_list, all_similarities)
    sim_x /= len(x_list)

    sim_y = 0
    for element in y_list:
        sim_y += most_similar(element, x_list, all_similarities)
    sim_y /= len(y_list)

    sim_xy = (sim_x + sim_y) / 2
    dist_xy = 1-sim_xy

    # print(attr_type)
    # print(dist_xy)

    return dist_xy


def distance(p1, p2, cl):
    #calculate sijk - similarity coefficient
    #retrieve deltaijk 0 or 1; what about weight?
    #numerator += sijk*deltaijk
    #denominator += deltaijk


    pd1 = p1.problem_definition
    pd2 = p2.problem_definition

    numerator = 0.0
    denominator = ALC_CAT_WEIGHT + sum(INGR_CAT_WEIGHTS)
    support = 0

    if "alcoholicPercentage" in pd1 and "alcoholicPercentage" in pd2:
        support += 1
        if pd1["alcoholicPercentage"] != pd2["alcoholicPercentage"]:
            if pd1["alcoholicPercentage"] == 'None' or pd2["alcoholicPercentage"] == 'None':
                if pd1["alcoholicPercentage"] == 'Low' or pd2["alcoholicPercentage"] == 'Low':
                    numerator += ALC_CAT_WEIGHT * 0.9
                else:
                    numerator += ALC_CAT_WEIGHT
            elif pd1["alcoholicPercentage"] == 'Low' or pd2["alcoholicPercentage"] == 'Low':
                if pd1["alcoholicPercentage"] == 'Medium' or pd2["alcoholicPercentage"] == 'Medium':
                    numerator += ALC_CAT_WEIGHT * 0.5
                else:
                    numerator += ALC_CAT_WEIGHT * 0.9
            else:
                numerator += ALC_CAT_WEIGHT * 0.5

    elif "alcoholicPercentage" not in pd1 and "alcoholicPercentage" not in pd2:
        numerator += ALC_CAT_WEIGHT


    for i, category in enumerate(cl.categories):
        if category in pd1 and category in pd2:
            numerator += INGR_CAT_WEIGHTS[i] * calculate_sijk(p1, p2, category, cl.sims[category])
            support += 1
        elif (category in pd1 and category not in pd2):
            n = len(p1.problem_definition[category])
            numerator += INGR_CAT_WEIGHTS[i] * (1.0 - float(1.0 / (n+1)))
        elif (category not in pd1 and category in pd2):
            n = len(p2.problem_definition[category])
            numerator += INGR_CAT_WEIGHTS[i] * (1.0 - float(1.0 / (n+1)))

    return float(numerator / denominator), support



def get_closest(problem, cl):
    '''
    Parameters:
        problem: a problem
        cl: a case library

    Returns:
        case, the most similar case to the given problem found in cases
    '''
    best_dist = float('inf')
    best_support = 0
    best_case = None
    for case in cl.cases:
        p2 = case.problem
        dist, support = distance(problem, p2, cl)
        if dist < best_dist:
            best_dist = dist
            best_support = support
            best_case = case
        elif dist == best_dist and support > best_support:
            best_dist = dist
            best_support = support
            best_case = case
    return best_dist, best_support, best_case
