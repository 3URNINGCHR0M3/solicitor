package net.forje.solicitor;

public class SolicitorApp {

    public static void main(String[] args) {

        String group = args[0];
        String artifact = args[1];

        try {
            Solicitor solicitor = SolicitorFactory.getInstance();
            solicitor.update(group, artifact);
        } catch (SolicitorException e) {
            e.printStackTrace();
        }

    }

}
