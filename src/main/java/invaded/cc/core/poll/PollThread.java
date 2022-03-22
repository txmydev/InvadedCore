package invaded.cc.core.poll;

import invaded.cc.core.Spotify;
import invaded.cc.core.util.Cooldown;

import java.util.Iterator;
import java.util.List;

public class PollThread extends Thread {

    public PollThread() {
        super("Spotify - Poll Thread");

        setDaemon(true);
    }

    @Override
    public void run() {
        while(true) {
            try{
                Iterator<Poll> iterator = Spotify.getInstance().getPollHandler().getPolls().iterator();

                while(iterator.hasNext()) {
                    Poll poll = iterator.next();
                    handleAnnounceTimer(poll);

                    if(poll.getTime().hasExpired()) {
                        poll.finish();
                        iterator.remove();
                    }
                }

                Thread.sleep(50L * 5L);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }


    private void handleAnnounceTimer(Poll poll) {
        if(poll.getAnnounceTimer().hasExpired()) {
            poll.announce();
            poll.setAnnounceTimer(new Cooldown(30000L));
        }
    }
}
