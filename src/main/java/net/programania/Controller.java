package net.programania;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import java.io.*;
import java.util.HashMap;

abstract class Controller {
  protected static final MustacheFactory mf = new DefaultMustacheFactory();

  protected static String render(HashMap<String, Object> scope, String tplName) {
    Writer writer = new StringWriter();
    BufferedReader reader = new BufferedReader(new InputStreamReader(Controller.class.getResourceAsStream(tplName)));
    Mustache mustache = mf.compile(reader, "index");
    mustache.execute(writer, scope);
    String output = writer.toString();
    try {
      writer.flush();
    } catch (IOException e) {
      output = String.format("EXCEPTION: %s\n%s", e.toString(), output);
    }
    return output;
  }
}
