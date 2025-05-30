package focusmind;

import java.util.Random;

public class Encoder {
    // Ascii letters: 65 to 122
    // Ascii numbers: 48 to 57
    public static String encode(String str, int len){
        if(str.length() > 1){
            String encoded_string = encode_two(str.substring(0, 2));
            int index = 2;
            while(index < str.length()){
                String new_two = 
                        Character.toString(encoded_string.charAt(encoded_string.length()-1))
                        +
                        Character.toString(str.charAt(index))
                ;
                encoded_string += encode_two(new_two);
                index++;
            }
            if(encoded_string.length() > len){
                int index_encoded_str = encoded_string.length()-1;
                int index_final_str = 0;
                while(encoded_string.length() != len){
                    if(index_final_str > (len-1)){
                        index_final_str = 0;
                    }
                    String new_letter = fusion_two_chars(
                            encoded_string.charAt(index_final_str), 
                            encoded_string.charAt(index_encoded_str)
                    );
                    encoded_string = replace_char_in(encoded_string, index_final_str, new_letter);
                    encoded_string = encoded_string.substring(0, index_encoded_str);
                    index_encoded_str--;
                    index_final_str++;
                }
            }else if(encoded_string.length() < len){
                index = encoded_string.length();
                while(index < len){
                    encoded_string += "0";
                    index++;
                }
            }
            return encoded_string;
        }else{
            System.err.println("Encoder: The string must be longer than two chars");
        }
        return null;
    }
    private static String replace_char_in(String str, int index, String replace){
        String str_final = "";
        if(index>0){
            str_final = str.substring(0, (index));
        }
        str_final += replace;
        if((index+1)<str.length()){
            str_final += str.substring((index+1), (str.length()));
        }
        return str_final;
    }
    private static String encode_two(String str){
        int sum = str.charAt(0)+str.charAt(1);
        int mul = str.charAt(0)*str.charAt(1);
        String new_string = "";
        if(mul % 2 == 0){
            new_string += Character.toString(encode_to_letter(sum)) 
                + 
                Character.toString(encode_to_number(mul)
            );
        }else{
            new_string += Character.toString(encode_to_number(sum))
                + 
                Character.toString(encode_to_letter(mul)
            );
        }
        return new_string;
    }
    private static char encode_to_letter(int val){
        int step = 0;
        if(57 < val){
            step = val % 57;
        }else{
            step = 57 % val;
        }
        char new_char = (char) (65+step);
        return new_char;
    }
    private static char encode_to_number(int val){
        int step = 0;
        if(10 < val){
            step = val % 10;
        }else{
            step = 10 % val;
        }
        char new_char = (char) (48+step);
        return new_char;
    }
    private static String fusion_two_chars(char one, char two){
        int letter = (int) Math.floor((one*two)/(one+two));
        if(!((65 <= letter && letter <= 122) || (48 <= letter && letter <= 57))){
            long num_ref1 = (long) (one*two);
            long num_ref2 = (long) (one+two);
            Random random1 = new Random(num_ref1);
            Random random2 = new Random(num_ref2);
            long num_ref_fin = random1.nextInt() + random2.nextInt();
            Random random = new Random(num_ref_fin);
            double factor = random.nextDouble();
            if(letter % 2 == 0){
                letter = (int) Math.floor((65 + factor*(56)));
            }else{
                letter = (int) Math.floor((48 + factor*(9)));
            }
        }
        char ret = (char) letter;
        return Character.toString(ret);
    }
}
