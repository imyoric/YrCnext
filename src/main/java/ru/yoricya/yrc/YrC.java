package ru.yoricya.yrc;

import sun.lwawt.macosx.CSystemTray;

import java.util.Arrays;
import java.util.HashMap;

public class YrC {
    private HashMap<String, Object> Vars = new HashMap<>();
    private HashMap<String, String> SimpleObjectNames = new HashMap<>();
    private boolean isInterfaceOpen = false;
    private String InterfaceScript = "";
    private String InterfaceVar;
    private YrC thin;
    public YrC(){
        thin = this;
        Vars.put("print", new ExecuteFunc(){
            @Override
            public YrcObject execute(String[] args) {
                String str = thin.parseStringExecutable(args[0], copyStrArrWithIndex(args, 1));
                System.out.println(str);

                return new YrcObject("yes");
            }
        });

        Vars.put("func", new ExecuteFunc(){
            @Override
            public YrcObject execute(String[] args) {
                isInterfaceOpen = true;
                InterfaceVar = args[1];
                return new YrcObject("yes");
            }
        });

        Vars.put("return", new ExecuteFunc(){
            @Override
            public YrcObject execute(String[] args) {
                return new YrcObject(true, parseStringExecutable(args[0], copyStrArrWithIndex(args, 1)));
            }
        });

        SimpleObjectNames.put(Vars.get("print").getClass().getSimpleName(), "executeFunction");
        SimpleObjectNames.put("".getClass().getSimpleName(), "String");
        SimpleObjectNames.put(((Integer)0).getClass().getSimpleName(), "Number");
        SimpleObjectNames.put(((Long)0L).getClass().getSimpleName(), "longNumber");
    }

    public YrcObject Parse(String Script){
        YrcObject returnObj = new YrcObject(new String("OK"));
        long codeLine = -1;

        for(String line : Script.split("\n")){
            codeLine++;

            if(line.trim().isEmpty())
                continue;

            if(isInterfaceOpen){
                if(line.equals("END")) {
                    isInterfaceOpen = false;
                    Vars.put(InterfaceVar, new ExecuteFunc() {
                        @Override
                        public YrcObject execute(String[] args) {
                            return null;
                        }
                    });
                    continue;
                }
                InterfaceScript += "\n"+line;
                continue;
            }

            line = line.replaceAll("^[ \t]+", "");
            String[] args = line.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

            if(args.length >= 3) if(args[1].equals("=")){
                Vars.put(args[0].trim(), parseStringExecutable(args[2], copyStrArrWithIndex(args, 2)));
                continue;
            }

            String parsedZeroArg = parseString(args[0]);

            if(parsedZeroArg == null)
                return new YrcObject(new YrCException("["+args[0]+":"+codeLine+"] Variable not set!"));

            if(parsedZeroArg.equals("executeFunction")){
                ExecuteFunc func = (ExecuteFunc) Vars.get(args[0]);
                YrcObject obj = func.execute(copyStrArrWithIndex(args, 1));
                if(obj.isReturned)
                    return obj;
            }else{
                if(args.length >= 3){
                    if(!(Vars.get(args[0]) instanceof String))
                        return new YrcObject(new YrCException("["+args[0]+":"+codeLine+"] Variable object not supported!"));

                    int val = 0;
                    String operating = args[1];

                    try {
                        val = Integer.parseInt((String) Vars.get(args[0]));
                    }catch (NumberFormatException e){
                        String str = null;
                        if(operating.equals("+="))
                            str = Vars.get(args[0]) + parseStringExecutable(args[2], copyStrArrWithIndex(args, 2));
                        else if(operating.equals("-="))
                            str = ((String) Vars.get(args[0])).replace(parseStringExecutable(args[2], copyStrArrWithIndex(args, 2)), "");
                        else
                            return new YrcObject(new YrCException("["+args[0]+":"+codeLine+"] Unsupported operation!"));

                        Vars.put(args[0], str);
                        continue;
                    }

                    int val2 = Integer.parseInt(parseStringExecutable(args[2], copyStrArrWithIndex(args, 2)));

                    if(operating.equals("+="))
                        val += val2;
                    else if(operating.equals("-="))
                        val -= val2;
                    else if(operating.equals("*="))
                        val *= val2;
                    else if(operating.equals("/="))
                        val /= val2;

                    Vars.put(args[0], String.valueOf(val));
                }
            }
        }

        return returnObj;
    }

    public static String[] copyStrArrWithIndex(String[] arr, int index) {
        if(arr.length - index <= 0) return new String[0];

        String[] result = new String[arr.length - index];

        int curindex = -1;
        int resindex = -1;

        for(String s : arr){
            curindex++;
            if(curindex < index) continue;

            resindex++;
            result[resindex] = s;
        }

        return result;
    }

    public String getSimpleYrcObjectName(Object obj){
        if(SimpleObjectNames.get(obj.getClass().getSimpleName()) == null)
            return obj.getClass().getSimpleName();

        return SimpleObjectNames.get(obj.getClass().getSimpleName());
    }

    public String parseString(String varOrString){
        varOrString = varOrString.trim();
        if(varOrString.matches("^\".*\"$")) {
            return varOrString.replaceAll("^\"|\"$", "");
        }else{
            Object Varobj = Vars.get(varOrString);

            if(Varobj == null)
                return null;

            if(!(Varobj instanceof String))
                return getSimpleYrcObjectName(Varobj);

            return (String) Varobj;
        }
    }

    public String parseStringExecutable(String varOrString, String[] args){
        varOrString = varOrString.trim();
        if(varOrString.matches("^\".*\"$")) {
            return varOrString.replaceAll("^\"|\"$", "");
        }else{
            Object Varobj = Vars.get(varOrString);

            if(Varobj == null)
                return null;

            if(Varobj instanceof ExecuteFunc)
                return (String) ((ExecuteFunc) Varobj).execute(args).object;

            if(!(Varobj instanceof String))
                return getSimpleYrcObjectName(Varobj);

            return (String) Varobj;
        }
    }

    public interface ExecuteFunc{
        YrcObject execute(String[] args);
    }

    public static class YrcObject{
        public final boolean isException;
        public final Object object;
        public boolean isReturned = false;
        public YrcObject(Object obj){
            object = obj;
            isException = obj instanceof YrCException;
        }
        public YrcObject(boolean re, Object obj){
            object = obj;
            isReturned = re;
            isException = obj instanceof YrCException;
        }
    }

    public static class YrCException extends Exception{
        public YrCException(String message) {
            super(message);
        }
    }
}
