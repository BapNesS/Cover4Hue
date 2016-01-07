package com.baptistecarlier.android.cover4hue.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.preference.PreferenceManager;

/**
 * Created by BapNesS on 06/01/2016.
 */
public class AndroidMusicIntentReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
	/*	Session s = LastFMApplication.getInstance().session;
		if (s != null && s.getKey().length() > 0 && PreferenceManager.getDefaultSharedPreferences(LastFMApplication.getInstance()).getBoolean("scrobble", true)) {
			if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				if (ScrobblerQueueDao.getInstance().getQueueSize()<1) {
					return;
				}
			}
			final Intent out = new Intent(context, ScrobblerService.class);
			out.setAction(intent.getAction());
			out.putExtras(intent);
			context.startService(out);
		} else if (s != null && s.getKey().length() > 0 && intent.getAction().equals("fm.last.android.LOVE")) {
			IBinder service = peekService(context, new Intent(context, RadioPlayerService.class));
			if (service == null) {
				return;
			}
			try {
				IRadioPlayer player = fm.last.android.player.IRadioPlayer.Stub.asInterface(service);
				if (player != null && player.isPlaying()) {
					String track = player.getTrackName();
					String artist = player.getArtistName();
					if (!track.equals(RadioPlayerService.UNKNOWN) && !artist.equals(RadioPlayerService.UNKNOWN)) {
						LastFmServer server = AndroidLastFmServerFactory.getServer();
						server.loveTrack(artist, track, LastFMApplication.getInstance().session.getKey());
						Toast.makeText(context, context.getString(R.string.scrobbler_trackloved), Toast.LENGTH_SHORT).show();
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (s != null && s.getKey().length() > 0 && intent.getAction().equals("fm.last.android.BAN")) {
			IBinder service = peekService(context, new Intent(context, RadioPlayerService.class));
			if (service == null) {
				return;
			}
			try {
				IRadioPlayer player = fm.last.android.player.IRadioPlayer.Stub.asInterface(service);
				if (player != null && player.isPlaying()) {
					String track = player.getTrackName();
					String artist = player.getArtistName();
					if (!track.equals(RadioPlayerService.UNKNOWN) && !artist.equals(RadioPlayerService.UNKNOWN)) {
						LastFmServer server = AndroidLastFmServerFactory.getServer();
						server.banTrack(artist, track, LastFMApplication.getInstance().session.getKey());
						Toast.makeText(context, context.getString(R.string.scrobbler_trackbanned), Toast.LENGTH_SHORT).show();
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		*/
	}

}
