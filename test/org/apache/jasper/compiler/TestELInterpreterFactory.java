/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jasper.compiler;

import java.io.File;

import javax.servlet.ServletContext;

import org.junit.Assert;
import org.junit.Test;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.startup.TomcatBaseTest;
import org.apache.jasper.JspCompilationContext;
import org.apache.jasper.compiler.ELInterpreterFactory.DefaultELInterpreter;

public class TestELInterpreterFactory extends TomcatBaseTest {

    public static class SimpleELInterpreter implements ELInterpreter {

        @Override
        public String interpreterCall(JspCompilationContext context,
                boolean isTagFile, String expression, Class<?> expectedType,
                String fnmapvar, boolean xmlEscape) {
            return expression;
        }
    }

    @Test
    public void testBug54239() throws Exception {
        Tomcat tomcat = getTomcatInstance();

        File appDir = new File("test/webapp");
        Context ctx = tomcat.addWebapp(null, "/test", appDir.getAbsolutePath());
        tomcat.start();

        ServletContext context = ctx.getServletContext();

        ELInterpreter interpreter =
                ELInterpreterFactory.getELInterpreter(context);
        Assert.assertNotNull(interpreter);
        Assert.assertTrue(interpreter instanceof DefaultELInterpreter);

        context.removeAttribute(ELInterpreter.class.getName());

        context.setAttribute(ELInterpreter.class.getName(),
                SimpleELInterpreter.class.getName());
        interpreter = ELInterpreterFactory.getELInterpreter(context);
        Assert.assertNotNull(interpreter);
        Assert.assertTrue(interpreter instanceof SimpleELInterpreter);

        context.removeAttribute(ELInterpreter.class.getName());

        SimpleELInterpreter simpleInterpreter = new SimpleELInterpreter();
        context.setAttribute(ELInterpreter.class.getName(), simpleInterpreter);
        interpreter = ELInterpreterFactory.getELInterpreter(context);
        Assert.assertNotNull(interpreter);
        Assert.assertTrue(interpreter instanceof SimpleELInterpreter);
        Assert.assertTrue(interpreter == simpleInterpreter);

        context.removeAttribute(ELInterpreter.class.getName());


        context.setInitParameter(ELInterpreter.class.getName(),
                SimpleELInterpreter.class.getName());

        interpreter = ELInterpreterFactory.getELInterpreter(context);
        Assert.assertNotNull(interpreter);
        Assert.assertTrue(interpreter instanceof SimpleELInterpreter);

        context.removeAttribute(ELInterpreter.class.getName());
    }
}
