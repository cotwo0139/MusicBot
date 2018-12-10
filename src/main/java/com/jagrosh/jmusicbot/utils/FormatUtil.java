/*
 * Copyright 2016 John Grosh <john.a.grosh@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jagrosh.jmusicbot.utils;

import java.util.List;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class FormatUtil {
    
    public static String formatTime(long duration)
    {
        if(duration == Long.MAX_VALUE)
            return "실시간 스트리밍";
        long seconds = Math.round(duration/1000.0);
        long hours = seconds/(60*60);
        seconds %= 60*60;
        long minutes = seconds/60;
        seconds %= 60;
        return (hours>0 ? hours+":" : "") + (minutes<10 ? "0"+minutes : minutes) + ":" + (seconds<10 ? "0"+seconds : seconds);
    }
    
    /*public static String embedFormat(AudioHandler handler)
    {
        if(handler==null)
            return "No music playing\n\u23F9 "+progressBar(-1)+" "+volumeIcon(100);
        else if (!handler.isMusicPlaying())
            return "No music playing\n\u23F9 "+progressBar(-1)+" "+volumeIcon(handler.getPlayer().getVolume());
        else
        {
            AudioTrack track = handler.getPlayer().getPlayingTrack();
            double progress = (double)track.getPosition()/track.getDuration();
            return (handler.getPlayer().isPaused()?"\u23F8":"\u25B6")
                    +" "+progressBar(progress)
                    +" `["+formatTime(track.getPosition()) + "/" + formatTime(track.getDuration()) +"]` "
                    +volumeIcon(handler.getPlayer().getVolume());
        }
    }//*/
        
    public static String progressBar(double percent)
    {
        String str = "";
        for(int i=0; i<12; i++)
            if(i == (int)(percent*12))
                str+="\uD83D\uDD18";
            else
                str+="▬";
        return str;
    }
    
    public static String volumeIcon(int volume)
    {
        if(volume == 0)
            return "\uD83D\uDD07";
        if(volume < 30)
            return "\uD83D\uDD08";
        if(volume < 70)
            return "\uD83D\uDD09";
        return "\uD83D\uDD0A";
    }
    
    public static String listOfTChannels(List<TextChannel> list, String query)
    {
        String out = " \""+query+"\" 에 일치하는 여러 텍스트 채널이 있네요!\n텍스트 채널들:";
        for(int i=0; i<6 && i<list.size(); i++)
            out+="\n - "+list.get(i).getName()+" (<#"+list.get(i).getId()+">)";
        if(list.size()>6)
            out+="\n**와 "+(list.size()-6)+" 개 더 있어요...**";
        return out;
    }
    
    public static String listOfVChannels(List<VoiceChannel> list, String query)
    {
        String out = " \""+query+"\" 에 일치하는 여러 음성 채널이 있네요!\n음성 채널들:";
        for(int i=0; i<6 && i<list.size(); i++)
            out+="\n - "+list.get(i).getName()+" (ID:"+list.get(i).getId()+")";
        if(list.size()>6)
            out+="\n**와 "+(list.size()-6)+" 개 더 있어요...**";
        return out;
    }
    
    public static String listOfRoles(List<Role> list, String query)
    {
        String out = " \""+query+"\" 에 일치하는 여러 역할들이 있네요!\n역할들:";
        for(int i=0; i<6 && i<list.size(); i++)
            out+="\n - "+list.get(i).getName()+" (ID:"+list.get(i).getId()+")";
        if(list.size()>6)
            out+="\n**와"+(list.size()-6)+" 개 더 있어요...**";
        return out;
    }
    
    public static String filter(String input)
    {
        return input.replace("@everyone", "@\u0435veryone").replace("@here", "@h\u0435re").trim();
    }
}
