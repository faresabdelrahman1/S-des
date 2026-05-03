import java.util.Scanner;
 public class Main {

            // ================= PERMUTATION TABLES =================

            static int[] P10 = {3, 5, 2, 7, 4, 10, 1, 9, 8, 6};

            static int[] P8 = {6, 3, 7, 4, 8, 5, 10, 9};

            static int[] IP = {2, 6, 3, 1, 4, 8, 5, 7};

            static int[] IP_INV = {4, 1, 3, 5, 7, 2, 8, 6};

            static int[] EP = {4, 1, 2, 3, 2, 3, 4, 1};

            static int[] P4 = {2, 4, 3, 1};

            // ================= S-BOXES =================

            static int[][] S0 = {
                    {1, 0, 3, 2},
                    {3, 2, 1, 0},
                    {0, 2, 1, 3},
                    {3, 1, 3, 2}
            };

            static int[][] S1 = {
                    {0, 1, 2, 3},
                    {2, 0, 1, 3},
                    {3, 0, 1, 0},
                    {2, 1, 0, 3}
            };

            static String K1;
            static String K2;

            // ================= PERMUTATION FUNCTION =================

            public static String permute(String input, int[] table) {

                StringBuilder result = new StringBuilder();

                for (int i = 0; i < table.length; i++) {
                    result.append(input.charAt(table[i] - 1));
                }

                return result.toString();
            }

            // ================= LEFT SHIFT FUNCTION =================

            public static String leftShift(String input, int shifts) {

                String shifted = input;

                for (int i = 0; i < shifts; i++) {
                    shifted = shifted.substring(1) + shifted.charAt(0);
                }

                return shifted;
            }

            // ================= XOR FUNCTION =================

            public static String xor(String a, String b) {

                StringBuilder result = new StringBuilder();

                for (int i = 0; i < a.length(); i++) {

                    if (a.charAt(i) == b.charAt(i)) {
                        result.append("0");
                    } else {
                        result.append("1");
                    }
                }

                return result.toString();
            }

            // ================= S-BOX FUNCTION =================

            public static String sBox(String input, int[][] sbox) {

                int row = Integer.parseInt("" + input.charAt(0) + input.charAt(3), 2);

                int col = Integer.parseInt("" + input.charAt(1) + input.charAt(2), 2);

                int value = sbox[row][col];

                return String.format("%2s",
                        Integer.toBinaryString(value)).replace(' ', '0');
            }

            // ================= FK FUNCTION =================

            public static String fk(String input, String key) {

                String left = input.substring(0, 4);

                String right = input.substring(4);

                // Expansion Permutation
                String expanded = permute(right, EP);

                // XOR with key
                String xorResult = xor(expanded, key);

                // Split
                String leftPart = xorResult.substring(0, 4);

                String rightPart = xorResult.substring(4);

                // S-Boxes
                String s0 = sBox(leftPart, S0);

                String s1 = sBox(rightPart, S1);

                // P4
                String p4 = permute(s0 + s1, P4);

                // XOR with left
                String leftResult = xor(left, p4);

                return leftResult + right;
            }

            // ================= SWAP FUNCTION =================

            public static String swap(String input) {

                return input.substring(4) + input.substring(0, 4);
            }

            // ================= KEY GENERATION =================

            public static void generateKeys(String key) {

                // P10
                String p10Key = permute(key, P10);

                System.out.println("\nAfter P10: " + p10Key);

                // Split
                String left = p10Key.substring(0, 5);

                String right = p10Key.substring(5);

                // LS-1
                left = leftShift(left, 1);

                right = leftShift(right, 1);

                System.out.println("After LS-1: " + left + " " + right);

                // K1
                String combined1 = left + right;

                K1 = permute(combined1, P8);

                System.out.println("K1: " + K1);

                // LS-2
                left = leftShift(left, 2);

                right = leftShift(right, 2);

                System.out.println("After LS-2: " + left + " " + right);

                // K2
                String combined2 = left + right;

                K2 = permute(combined2, P8);

                System.out.println("K2: " + K2);
            }

            // ================= ENCRYPTION =================

            public static String encrypt(String plaintext) {

                // Initial Permutation
                String ip = permute(plaintext, IP);

                // Round 1 with K1
                String round1 = fk(ip, K1);

                // Swap
                String swapped = swap(round1);

                // Round 2 with K2
                String round2 = fk(swapped, K2);

                // Final Permutation
                return permute(round2, IP_INV);
            }

            // ================= DECRYPTION =================

            public static String decrypt(String ciphertext) {

                // Initial Permutation
                String ip = permute(ciphertext, IP);

                // Round 1 with K2
                String round1 = fk(ip, K2);

                // Swap
                String swapped = swap(round1);

                // Round 2 with K1
                String round2 = fk(swapped, K1);

                // Final Permutation
                return permute(round2, IP_INV);
            }

            // ================= MAIN =================

            public static void main(String[] args) {

                Scanner sc = new Scanner(System.in);

                // ===== INPUT KEY =====
                System.out.print("Enter 10-bit key: ");

                String key = sc.nextLine();

                if (key.length() != 10 || !key.matches("[01]+")) {

                    System.out.println("Invalid Key!");
                    return;
                }

                // ===== GENERATE KEYS =====
                generateKeys(key);

                // ===== INPUT PLAINTEXT =====
                System.out.print("\nEnter 8-bit plaintext: ");

                String plaintext = sc.nextLine();

                if (plaintext.length() != 8 || !plaintext.matches("[01]+")) {

                    System.out.println("Invalid Plaintext!");
                    return;
                }

                // ===== ENCRYPT =====
                String cipher = encrypt(plaintext);

                System.out.println("\nEncrypted Text: " + cipher);

                // ===== DECRYPT =====
                String decrypted = decrypt(cipher);

                System.out.println("Decrypted Text: " + decrypted);

                sc.close();
            }
        }

