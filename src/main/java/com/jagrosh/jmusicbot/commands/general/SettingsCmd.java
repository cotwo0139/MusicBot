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
package com.jagrosh.jmusicbot.commands.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.settings.Settings;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class SettingsCmd extends Command 
{
    public SettingsCmd()
    {
        this.name = "settings";
        this.help = "봇의 설정을 보여줘요!";
        this.aliases = new String[]{"status","설정","ㄴㄷㅅ샤ㅜㅎㄴ","tjfwjd","tjqjtjjfwjd"};
        this.guildOnly = true;
    }
    
    @Override
    protected void execute(CommandEvent event) 
    {
        Settings s = event.getClient().getSettingsFor(event.getGuild());
        MessageBuilder builder = new MessageBuilder()
                .append("\uD83C\uDFA7 **")
                .append(event.getSelfUser().getName())
                .append("** 설정들:");
        TextChannel tchan = s.getTextChannel(event.getGuild());
        VoiceChannel vchan = s.getVoiceChannel(event.getGuild());
        Role role = s.getRole(event.getGuild());
        EmbedBuilder ebuilder = new EmbedBuilder()
                .setColor(event.getSelfMember().getColor())
                .setDescription("기본 텍스트 채널: "+(tchan==null ? "모든 곳!" : "**#"+tchan.getName()+"**")
                        + "\n보이스 채널: "+(vchan==null ? "모든 곳!" : "**"+vchan.getName()+"**")
                        + "\nDJ 역할: "+(role==null ? "없음" : "**"+role.getName()+"**")
                        + "\n반복 모드: **"+(s.getRepeatMode() ? "활성화" : "비활성화")+"**"
                        + "\n기본 플레이리스트: "+(s.getDefaultPlaylist()==null ? "존재하지 않아요!" : "**"+s.getDefaultPlaylist()+"**")
                        )
                .setFooter(event.getJDA().getGuilds().size()+" 서버들 | "
                        +event.getJDA().getGuilds().stream().filter(g -> g.getSelfMember().getVoiceState().inVoiceChannel()).count()
                        +" 오디오 연결들", null);
        event.getChannel().sendMessage(builder.setEmbed(ebuilder.build()).build()).queue();
    }
    
}
