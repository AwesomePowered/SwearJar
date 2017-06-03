def main():
    curseWord = input("Enter a Curse Word: ").lower()
    if " " in curseWord:
        print("You cannot have spaces!")
        return

    print("\nPlease add the contents below to your rules.yml\n")
    print(curseWord + ":\n  match: '" + makeFilter(curseWord)+"'\n")

def makeFilter(curseWord):
    return "\\b("+"+(\W|\d|_)*".join([curseWord[i:i+1] for i in range(0, len(curseWord)+1,1)])+")"

main()