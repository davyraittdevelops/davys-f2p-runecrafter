package scripts;

public class Utils {

    public static String formatTime(long time) {
        long seconds = (time / 1000) % 60;
        long minutes = (time / (1000 * 60)) % 60;
        long hours = (time / (1000 * 60 * 60)) % 24;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static String getRequiredTiara(String runeType) {
        switch (runeType) {
            case "Craft air runes":
                return "Air tiara";
            case "Craft earth runes":
                return "Earth tiara";
            case "Craft fire runes":
                return "Fire tiara";
            case "Craft body runes":
                return "Body tiara";
            default:
                return null; // No tiara required or unrecognized rune type
        }
    }





}
