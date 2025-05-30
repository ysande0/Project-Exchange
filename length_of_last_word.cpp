#include <iostream>
#include <string>

/*
* 58. Length of Last Word


Given a string s consisting of words and spaces, return the length of the last word in the string.

A word is a maximal
consisting of non-space characters only.
*
*/


using std::cout;
using std::endl;
using std::string;

int length_of_string(string & s);

int main()
{
    string s = "Hello World";
    
    cout << "Length of last word: " << length_of_string(s) << endl;
    return 0;
}

int length_of_string(string & s)
{
    
    bool is_word_found = false;
    int counter = 0;
    int i = s.size() - 1;
    
    while(!is_word_found)
    {
              
        if(i <= 0)
            is_word_found = true;
            
        if(s[i] == ' ')
        {
            
            if(counter > 0)
                is_word_found = true;
            
        }
        else 
            counter++;
            
        i--;
  
    }
    
    return counter;
}