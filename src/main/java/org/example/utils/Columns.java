package org.example.util;

public class Columns {
    // Devuelve una línea con descripción izquierda y valor derecha ajustado al ancho
    public static String lr(String left, String right, int width) {
        if (left == null) left = "";
        if (right == null) right = "";
        int gap = width - right.length();
        if (gap <= 0) return left; // fallback
        String l = left;
        if (l.length() > gap) l = l.substring(0, Math.max(0, gap-1));
        return padRight(l, gap) + right;
    }

    public static String padRight(String s, int width) {
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < width) sb.append(' ');
        return sb.toString();
    }

    public static String line(int width, char c) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < width; i++) sb.append(c);
        return sb.toString();
    }
}
