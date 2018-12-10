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

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.QueuedTrack;
import com.jagrosh.jmusicbot.commands.MusicCommand;
import com.jagrosh.jmusicbot.settings.Settings;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class RemoveCmd extends MusicCommand 
{
    public RemoveCmd(Bot bot)
    {
        super(bot);
        this.name = "remove";
        this.help = "대기열에서 노래를 지워요!";
        this.arguments = "<위치|모두>";
        this.aliases = new String[]{"delete","삭제","ㄱ드ㅐㅍㄷ","제거","지우기","wldnrl","tkrwp","wprj","ㅇ딛ㅅㄷ"};
        this.beListening = true;
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event) 
    {
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        if(handler.getQueue().isEmpty())
        {
            event.replyError("대기열에 아무 노래도 존재하지 않아요!");
            return;
        }
        if(event.getArgs().equalsIgnoreCase("모두") || event.getArgs().equalsIgnoreCase("모두"))
        {
            int count = handler.getQueue().removeAll(event.getAuthor().getIdLong());
            if(count==0)
                event.replyWarning("아무 노래도 대기열에 존재하지 않아요!");
            else
                event.replySuccess("성공적으로 "+count+" 개의 노래를 지웠어요!");
            return;
        }
        int pos;
        try {
            pos = Integer.parseInt(event.getArgs());
        } catch(NumberFormatException e) {
            pos = 0;
        }
        if(pos<1 || pos>handler.getQueue().size())
        {
            event.replyError("위치는 1 부터 "+handler.getQueue().size()+" 까지의 정수여야 해요!");
            return;
        }
        Settings settings = event.getClient().getSettingsFor(event.getGuild());
        boolean isDJ = event.getMember().hasPermission(Permission.MANAGE_SERVER);
        if(!isDJ)
            isDJ = event.getMember().getRoles().contains(settings.getRole(event.getGuild()));
        QueuedTrack qt = handler.getQueue().get(pos-1);
        if(qt.getIdentifier()==event.getAuthor().getIdLong())
        {
            handler.getQueue().remove(pos-1);
            event.replySuccess("**"+qt.getTrack().getInfo().title+"** 을(를) 대기열에서 지웠어요!");
        }
        else if(isDJ)
        {
            handler.getQueue().remove(pos-1);
            User u;
            try {
                u = event.getJDA().getUserById(qt.getIdentifier());
            } catch(Exception e) {
                u = null;
            }
            event.replySuccess("**"+qt.getTrack().getInfo().title
                    +"** 을(를) 대기열에서 지워요! (신청자: "+(u==null ? "누군가" : "**"+u.getName()+"**")+")");
        }
        else
        {
            event.replyError("당신은 이 노래 **"+qt.getTrack().getInfo().title+"** 을(를) 추가하지 않으셔서 지우실수 없어요!");
        }
    }
}
