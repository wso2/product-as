package org.wso2.appserver.sample.ee.jsf.ejb;

import java.util.HashSet;
import java.util.Set;
import javax.ejb.Stateless;

@Stateless
public class Analyzer {

    public String analyzeName(String originalText) {

        Set<Character> set = new HashSet<Character>();

        for (char c : originalText.toLowerCase().toCharArray()) {
            if (c == ' ')
                continue;
            set.add(c);
        }

        String result = "";
        for (char c : set) {
            result += ((result == "") ? "" : ", ") + c;
        }

        return result;
    }
}
