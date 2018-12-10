/*
 * Copyright 2017 John Grosh <john.a.grosh@gmail.com>.
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
package com.jagrosh.jmusicbot.commands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.commands.OwnerCommand;
import net.dv8tion.jda.core.entities.Game;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class SetgameCmd extends OwnerCommand
{
    public SetgameCmd()
    {
        this.name = "setgame";
        this.help = "봇이 플레이하는 게임을 설정해요!";
        this.arguments = "[활동] [게임]";
        this.guildOnly = false;
        this.children = new OwnerCommand[]{
            new SetlistenCmd(),
            new SetstreamCmd(),
            new SetwatchCmd()
        };
    }
    
    @Override
    protected void execute(CommandEvent event) 
    {
        String title = event.getArgs().toLowerCase().startsWith("playing") ? event.getArgs().substring(7).trim() : event.getArgs();
        try
        {
            event.getJDA().getPresence().setGame(title.isEmpty() ? null : Game.playing(title));
            event.reply(event.getClient().getSuccess()+" **"+event.getSelfUser().getName()
                    +"** 는 이제 "+(title.isEmpty() ? "아무것도 플레이하지 않아요!" : "`"+title+"` 를 플레이해요!"));
        }
        catch(Exception e)
        {
            event.reply(event.getClient().getError()+" 플레이하는 게임이 설정되지 않았어요!");
        }
    }
    
    private class SetstreamCmd extends OwnerCommand
    {
        private SetstreamCmd()
        {
            this.name = "stream";
            this.aliases = new String[]{"twitch","streaming"};
            this.help = "봇을 스트리밍 상태로 설정해요!";
            this.arguments = "<트위치 유저 이름> <게임>";
            this.guildOnly = false;
        }

        @Override
        protected void execute(CommandEvent event)
        {
            String[] parts = event.getArgs().split("\\s+", 2);
            if(parts.length<2)
            {
                event.replyError("트위치 유저 이름과 게임 이름을 포함해주세요!");
                return;
            }
            try
            {
                event.getJDA().getPresence().setGame(Game.streaming(parts[1], "https://twitch.tv/"+parts[0]));
                event.replySuccess("**"+event.getSelfUser().getName()
                        +"** 는 이제 `"+parts[1]+"` 을(를) 방송 중 이에요!");
            }
            catch(Exception e)
            {
                event.reply(event.getClient().getError()+" 플레이하는 게임이 설정되지 않았어요!");
            }
        }
    }
    
    private class SetlistenCmd extends OwnerCommand
    {
        private SetlistenCmd()
        {
            this.name = "listen";
            this.aliases = new String[]{"listening"};
            this.help = "봇의 게임 상태를 듣기로 설정해요!";
            this.arguments = "<제목>";
            this.guildOnly = false;
        }

        @Override
        protected void execute(CommandEvent event)
        {
            if(event.getArgs().isEmpty())
            {
                event.replyError("듣는 노래의 제목을 포함해주세요!");
                return;
            }
            String title = event.getArgs().toLowerCase().startsWith("to") ? event.getArgs().substring(2).trim() : event.getArgs();
            try
            {
                event.getJDA().getPresence().setGame(Game.listening(title));
                event.replySuccess("**"+event.getSelfUser().getName()+"** 는 이제 `"+title+"` 을(를) 듣고 있어요!");
            } catch(Exception e) {
                event.reply(event.getClient().getError()+" 플레이하는 게임이 설정되지 않았어요!");
            }
        }
    }
    
    private class SetwatchCmd extends OwnerCommand
    {
        private SetwatchCmd()
        {
            this.name = "watch";
            this.aliases = new String[]{"watching"};
            this.help = "봇의 무언가를 보고있는 상태로 설정해요!";
            this.arguments = "<제목>";
            this.guildOnly = false;
        }

        @Override
        protected void execute(CommandEvent event)
        {
            if(event.getArgs().isEmpty())
            {
                event.replyError("보고있는 것의 제목을 포함해주세요!");
                return;
            }
            String title = event.getArgs();
            try
            {
                event.getJDA().getPresence().setGame(Game.watching(title));
                event.replySuccess("**"+event.getSelfUser().getName()+"** 는 지금 `"+title+"` 을(를) 보고있는 상태로 설정되었어요!");
            } catch(Exception e) {
                event.reply(event.getClient().getError()+" 플레이하는 게임이 설정되지 않았어요!");
            }
        }
    }
}
