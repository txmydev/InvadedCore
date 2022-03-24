package invaded.cc.core.nametag;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NametagUpdateThread extends Thread {

    private final NametagManager nametagManager;
    private final int exceptionLimit = 5;

    @Override
    public void run() {
        int exceptionsOccurred = 0;
        while(true) {
            try{
                if(nametagManager.getProvider() == null) return;

                nametagManager.updateAll();
                Thread.sleep(50L * nametagManager.getProvider().getUpdateInterval());
            }catch(Exception ex) {
                ex.printStackTrace();
                if(exceptionsOccurred++ >= exceptionLimit){
                    break;
                }
                break;
            }
        }
    }
}
