import os


def file_read(filepath):
    f = open(filepath)
    content = f.readlines()
    f.close()
    return content

def file_write(filepath, content):
    f = open(filepath,"w")
    f.writelines(content)
    f.close()


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

def write_proposal_to_file(cl, proposal_str,problem_str,solution, should_include_alcohol):
    folder_path = os.path.realpath(__file__)[0:os.path.realpath(__file__).rfind("/")]
    proposal_file = '/../proposals.txt'
    if not os.path.exists(folder_path + proposal_file):
        f = open(folder_path + proposal_file, "w+")
        f.write("<proposals>\n</proposals>")
        f.close()
    content = file_read(folder_path + proposal_file)
    f = open(folder_path + proposal_file, "w")
    content[-1] = "<proposal>\n"
    content.append("<text>" + proposal_str +"</text>\n")
    content.append("<problem>" + problem_str + "</problem>\n")
    from case_library.CaseLibrary import CaseLibrary
    content.append("<solution>\n" + cl.xml_encode_cocktail(solution,should_include_alcohol)+"</solution>\n")
    content.append("</proposal>\n")
    content.append("</proposals>")
    f.writelines(content)
    f.close()
