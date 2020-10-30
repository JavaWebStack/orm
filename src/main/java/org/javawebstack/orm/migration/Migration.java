package org.javawebstack.orm.migration;

public interface Migration {

    String version();

    default String name(){
        String className = getClass().getSimpleName();
        StringBuilder sb = new StringBuilder(Character.toLowerCase(className.charAt(0)));
        for(int i=1; i < className.length(); i++){
            if(Character.isUpperCase(className.charAt(i))){
                sb.append('-');
                sb.append(Character.toLowerCase(className.charAt(i)));
            }else{
                sb.append(className.charAt(i));
            }
        }
        return version().replace(" ", "-") + "-" + sb.toString();
    }

    void up(DB db);

    void down(DB db);

}
