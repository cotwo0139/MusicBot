/*
 * Copyright 2018 John Grosh <john.a.grosh@gmail.com>.
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
package com.jagrosh.jmusicbot.commands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jlyrics.Lyrics;
import com.jagrosh.jlyrics.LyricsClient;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.commands.MusicCommand;
import java.util.concurrent.ExecutionException;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class LyricsCmd extends MusicCommand
{
    private final LyricsClient client = new LyricsClient();
    
    public LyricsCmd(Bot bot)
    {
        super(bot);
        this.name = "lyrics";
        this.help = "현재 재생중인 노래의 가사를 검색해요!";
        this.aliases = new String[]{"nplyrics","가사"};
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event)
    {
        String title = ((AudioHandler)event.getGuild().getAudioManager().getSendingHandler()).getPlayer().getPlayingTrack().getInfo().title;
        Lyrics lyrics;
        try
        {
            lyrics = client.getLyrics(title).get();
        }
        catch(InterruptedException | ExecutionException ex)
        {
            lyrics = null;
        }
        
        if(lyrics == null)
        {
            event.replyError(title + " 노래의 가사를 찾을수 없어요!");
            return;
        }
        if(lyrics.getContent().length() > 1500) {
        	event.reply(new EmbedBuilder().setColor(event.getSelfMember().getColor())
                    .setAuthor(lyrics.getAuthor())
                    .setTitle(lyrics.getTitle(), lyrics.getURL())
                    .setDescription(lyrics.getContent().substring(0,1500)+"...")
                    .setFooter("가사의 정보는 정확하지 않을 수 있어요!", event.getSelfUser().getAvatarUrl()).build());
        } else if(lyrics.getContent().length() < 1500) {
        	event.reply(new EmbedBuilder().setColor(event.getSelfMember().getColor())
                    .setAuthor(lyrics.getAuthor())
                    .setTitle(lyrics.getTitle(), lyrics.getURL())
                    .setDescription(lyrics.getContent().substring(0,lyrics.getContent().length()))
                    .setFooter("가사의 정보는 정확하지 않을 수 있어요!", event.getSelfUser().getAvatarUrl()).build());
        }
    }
}
