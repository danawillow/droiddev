package com.droiddev.server;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class ListFiles extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
		      throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		out.println("[");

        String[] paths = {"HelloAndroid/res",
                          "HelloAndroid/src/com/example/helloandroid"};

        boolean first = true;

        for (String path: paths) {
            if (!first) out.print(", ");
            else first = false;

            listthings(path, out);
        }

        out.println("]");
        out.flush();
	}
	
    public static void listthings(String path, PrintWriter out) {
        out.print("{\n'name': '");
        File dir = new File(path);
        if (dir.isDirectory()) out.print(path);
        else out.print(dir.getName());
        out.print("',");
        out.print("'children': [");

        boolean first = true;
        if (dir.isDirectory()) {
            for (File f: dir.listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        if (name.charAt(0) == '.') return false;
                        if (name.charAt(name.length()-1) == '~') return false;
                        return true;
                    }
                })) {
                if (!first) out.print(", ");
                else first = false;

                listthings(f.getPath(), out);
            }
        }
        out.println("]}");
    }
}
