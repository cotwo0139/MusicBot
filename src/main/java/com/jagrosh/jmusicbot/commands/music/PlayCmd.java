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
package com.jagrosh.jmusicbot.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.menu.ButtonMenu;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.QueuedTrack;
import com.jagrosh.jmusicbot.commands.MusicCommand;
import com.jagrosh.jmusicbot.playlist.PlaylistLoader.Playlist;
import com.jagrosh.jmusicbot.settings.Settings;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.exceptions.PermissionException;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class PlayCmd extends MusicCommand
{
    public final static String LOAD = "\uD83D\uDCE5";
    public final static String CANCEL = "\uD83D\uDEAB";
    
    private final String loadingEmoji;
    
    public PlayCmd(Bot bot, String loadingEmoji)
    {
        super(bot);
        this.loadingEmoji = loadingEmoji;
        this.name = "play";
        this.arguments = "<제목|주소|다른 명령어>";
        this.help = "노래를 재생해요!";
        this.beListening = true;
        this.aliases = new String[]{"ㅔㅣ묘","재생","노래","플래이","플레이","wotod","vmffodl","vmffpdl","shfo"};
        this.bePlaying = false;
        this.children = new Command[]{new PlaylistCmd(bot)};
    }

    @Override
    public void doCommand(CommandEvent event) 
    {
        if(event.getArgs().isEmpty() && event.getMessage().getAttachments().isEmpty())
        {
            AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
            if(handler.getPlayer().getPlayingTrack()!=null && handler.getPlayer().isPaused())
            {
                boolean isDJ = event.getMember().hasPermission(Permission.MANAGE_SERVER);
                if(!isDJ)
                    isDJ = event.isOwner();
                Settings settings = event.getClient().getSettingsFor(event.getGuild());
                Role dj = settings.getRole(event.getGuild());
                if(!isDJ && dj!=null)
                    isDJ = event.getMember().getRoles().contains(dj);
                if(!isDJ)
                    event.replyError("오로지 DJ 만 일시 정지를 해제할 수 있어요!");
                else
                {
                    handler.getPlayer().setPaused(false);
                    event.replySuccess(" **"+handler.getPlayer().getPlayingTrack().getInfo().title+"** 를 다시 재생해요!");
                }
                return;
            }
            StringBuilder builder = new StringBuilder(event.getClient().getWarning()+" 재생 명령어들:\n");
            builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" <노래 제목>` - 유튜브 검색 결과 첫번째 노래를 재생해요!");
            builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" <주소>` - 주소의 노래나 스트리밍, 플레이리스트를 재생해요!");
            for(Command cmd: children)
                builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" ").append(cmd.getName()).append(" ").append(cmd.getArguments()).append("` - ").append(cmd.getHelp());
            event.reply(builder.toString());
            return;
        }
        String args = event.getArgs().startsWith("<") && event.getArgs().endsWith(">") 
                ? event.getArgs().substring(1,event.getArgs().length()-1) 
                : event.getArgs().isEmpty() ? event.getMessage().getAttachments().get(0).getUrl() : event.getArgs();
        event.reply(loadingEmoji+" 로딩 중... `["+args+"]`", m -> bot.getPlayerManager().loadItemOrdered(event.getGuild(), args, new ResultHandler(m,event,false)));
    }
    
    private class ResultHandler implements AudioLoadResultHandler
    {
        private final Message m;
        private final CommandEvent event;
        private final boolean ytsearch;
        
        private ResultHandler(Message m, CommandEvent event, boolean ytsearch)
        {
            this.m = m;
            this.event = event;
            this.ytsearch = ytsearch;
        }
        
        private void loadSingle(AudioTrack track, AudioPlaylist playlist)
        {
            if(bot.getConfig().isTooLong(track))
            {
                m.editMessage(FormatUtil.filter(event.getClient().getWarning()+" 이 곡 (**"+track.getInfo().title+"**) 은(는) 허용된 곡 길이보다 길어요! : `"
                        +FormatUtil.formatTime(track.getDuration())+"` > `"+FormatUtil.formatTime(bot.getConfig().getMaxSeconds()*1000)+"`")).queue();
                return;
            }
            AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
            int pos = handler.addTrack(new QueuedTrack(track, event.getAuthor()))+1;
            String addMsg = FormatUtil.filter(event.getClient().getSuccess()+" **"+track.getInfo().title
                    +"** (`"+FormatUtil.formatTime(track.getDuration())+"`) 곡이 "+(pos==0?"곧 재생돼요!":" 대기열 "+pos+" 번에 추가되었어요!"));
            if(playlist==null || !event.getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_ADD_REACTION))
                m.editMessage(addMsg).queue();
            else
            {
                new ButtonMenu.Builder()
                        .setText(addMsg+"\n"+event.getClient().getWarning()+" 이 곡의 재생 목록에는 **"+playlist.getTracks().size()+"** 곡이 포함되어 있어요! "+LOAD+" 를 선택하여 로드하실수 있어요!")
                        .setChoices(LOAD, CANCEL)
                        .setEventWaiter(bot.getWaiter())
                        .setTimeout(30, TimeUnit.SECONDS)
                        .setAction(re ->
                        {
                            if(re.getName().equals(LOAD))
                                m.editMessage(addMsg+"\n"+event.getClient().getSuccess()+" 추가적인 곡 **"+loadPlaylist(playlist, track)+"** 개를 로드했어요!").queue();
                            else
                                m.editMessage(addMsg).queue();
                        }).setFinalAction(m ->
                        {
                            try{m.clearReactions().queue();}catch(PermissionException ex){}
                        }).build().display(m);
            }
        }
        
        private int loadPlaylist(AudioPlaylist playlist, AudioTrack exclude)
        {
            int[] count = {0};
            playlist.getTracks().stream().forEach((track) -> {
                if(!bot.getConfig().isTooLong(track) && !track.equals(exclude))
                {
                    AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
                    handler.addTrack(new QueuedTrack(track, event.getAuthor()));
                    count[0]++;
                }
            });
            return count[0];
        }
        
        @Override
        public void trackLoaded(AudioTrack track)
        {
            loadSingle(track, null);
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist)
        {
            if(playlist.getTracks().size()==1 || playlist.isSearchResult())
            {
                AudioTrack single = playlist.getSelectedTrack()==null ? playlist.getTracks().get(0) : playlist.getSelectedTrack();
                loadSingle(single, null);
            }
            else if (playlist.getSelectedTrack()!=null)
            {
                AudioTrack single = playlist.getSelectedTrack();
                loadSingle(single, playlist);
            }
            else
            {
                int count = loadPlaylist(playlist, null);
                if(count==0)
                {
                    m.editMessage(FormatUtil.filter(event.getClient().getWarning()+" 이 재생 목록의 모든 항목 "+(playlist.getName()==null ? "" : "(**"+playlist.getName()
                            +"**) ")+"은 허용된 길이보다 길어요! (`"+bot.getConfig().getMaxTime()+"`)")).queue();
                }
                else
                {
                    m.editMessage(FormatUtil.filter(event.getClient().getSuccess()
                    		+(playlist.getName()==null?"재생 목록":"재생 목록 **"+playlist.getName()+"**")+" 와 `"
                            + playlist.getTracks().size()+"` 개가 대기열에 추가되었어요!"
                            + (count<playlist.getTracks().size() ? "\n"+event.getClient().getWarning()+" 개의 곡이 (`"
                            + bot.getConfig().getMaxTime()+"`) 허용된 길이보다 길어 생략되었어요!" : ""))).queue();
                }
            }
        }

        @Override
        public void noMatches()
        {
            if(ytsearch)
                m.editMessage(FormatUtil.filter(event.getClient().getWarning()+" `"+event.getArgs()+"` 에 대한 검색 결과를 찾지 못했어요!")).queue();
            else
                bot.getPlayerManager().loadItemOrdered(event.getGuild(), "ytsearch:"+event.getArgs(), new ResultHandler(m,event,true));
        }

        @Override
        public void loadFailed(FriendlyException throwable)
        {
            if(throwable.severity==Severity.COMMON)
                m.editMessage(event.getClient().getError()+" 로딩에 에러가 발생했어요! "+throwable.getMessage()).queue();
            else
                m.editMessage(event.getClient().getError()+" 곡을 로딩하는데 에러가 발생했어요!").queue();
        }
    }
    
    public class PlaylistCmd extends MusicCommand
    {
        public PlaylistCmd(Bot bot)
        {
            super(bot);
            this.name = "playlist";
            this.aliases = new String[]{"pl","ㅔㅣ묘ㅣㅑㄴㅅ","ㅔㅣ"};
            this.arguments = "<이름>";
            this.help = "제공된 재생 목록을 재생해요!";
            this.beListening = true;
            this.bePlaying = false;
        }

        @Override
        public void doCommand(CommandEvent event) 
        {
            if(event.getArgs().isEmpty())
            {
                event.reply(event.getClient().getError()+" 재생 목록의 이름을 포함해주세요!");
                return;
            }
            Playlist playlist = bot.getPlaylistLoader().getPlaylist(event.getArgs());
            if(playlist==null)
            {
                event.replyError("저는 `"+event.getArgs()+".txt` 를 재생 목록 폴더에서 찾지 못했어요!");
                return;
            }
            event.getChannel().sendMessage(loadingEmoji+" 재생 목록 **"+event.getArgs()+"** 을 로드 중이에요... ("+playlist.getItems().size()+" 개)").queue(m -> 
            {
                AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
                playlist.loadTracks(bot.getPlayerManager(), (at)->handler.addTrack(new QueuedTrack(at, event.getAuthor())), () -> {
                    StringBuilder builder = new StringBuilder(playlist.getTracks().isEmpty() 
                            ? event.getClient().getWarning()+" 곡이 로드되지 않았어요!" 
                            : event.getClient().getSuccess()+" **"+playlist.getTracks().size()+"** 개의 곡이 로드되었어요!");
                    if(!playlist.getErrors().isEmpty())
                        builder.append("\n이 곡들은 로드되지 않았어요:");
                    playlist.getErrors().forEach(err -> builder.append("\n`[").append(err.getIndex()+1).append("]` **").append(err.getItem()).append("**: ").append(err.getReason()));
                    String str = builder.toString();
                    if(str.length()>2000)
                        str = str.substring(0,1994)+" (...)";
                    m.editMessage(FormatUtil.filter(str)).queue();
                });
            });
        }
    }
}
