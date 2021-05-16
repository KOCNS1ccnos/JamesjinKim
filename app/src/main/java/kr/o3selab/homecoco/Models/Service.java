package kr.o3selab.homecoco.Models;

public class Service {

    public static final String HOME_SERVICE = "HomeService";
    public static final String REPAIR_SERVICE = "RepairService";
    public static final String INTERIOR_SERVICE = "InteriorService";
    public static final String QUESTION_SERVICE = "QuestionService";
    public static final String SOLUTION_SERVICE = "SolutionService";
    public static final String EBF_SERVICE = "EBFService";

    public static String[] getServices() {
        return new String[]{HOME_SERVICE, REPAIR_SERVICE, INTERIOR_SERVICE, QUESTION_SERVICE, SOLUTION_SERVICE, EBF_SERVICE};
    }

}
