package urm.Views;

/**
 * Created by Денис on 26.08.2016.
 */
public class HelpMessage {
    public static String getHelp(){
        String helpMessage = " unlimited register machine(further URM).\n" +
                "It has 4 command\n" +
                "The number recorded in the register R_n will be denoted\n" +
                "by r_n contents\n" +
                "1) reset command - Z (n) (n ∈ {1,2,3 ...}).\n" +
                "This command replaces the contents of the register R_n\n" +
                "to 0 without affecting the other registers.\n" +
                "2) increment 1 command - S(n) (n ∈ {1,2,3 ...})\n" +
                "This command increases the value of the register R_n by\n" +
                "1 without affecting other registers.\n" +
                "3) redirect command - T (m, n) (m, n ∈ {1,2,3 ...}).\n" +
                "This command replaces the contents of the register number R_n\n" +
                "r_m, contained in the register R_m without affecting other\n" +
                "registers (including the register R_m)\n" +
                "4)the conditional branch command is J (m, n, q) (m, n, q ∈ {1,2,3 ...}).\n" +
                "Execution of this command is as follows: - Compares the contents of the registers\n" +
                "R_m and R_n,if r_m = r_n the team proceeds to qy team running program; If r_m ≠ r_n\n" +
                "the program proceeds to the next command.If you want to run the command with a number\n" +
                "that exceeds the number of teams in the program, the machine stops working.\n" +
                "\n" +
                "Button \"Save\" -saves code to a specified location\n" +
                "Button \"Open\" - opens the code from a designated place\n" +
                "Button \"Compile\" - it compiles the code and checks for errors\n" +
                "Button \"Play\" -  starts executing  program\n" +
                "Button \"Step\" - the execution step by step\n" +
                "Button \"Stop\" - it stops the execution of the program\n" +
                "Button \"Reset\" - resets all";
        return helpMessage;
    }
}
