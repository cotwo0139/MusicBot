/*
 * Copyright 2016-2018 John Grosh (jagrosh) & Kaidan Gustave (TheMonitorLizard)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jagrosh.jmusicbot.commands.general;

import java.time.temporal.ChronoUnit;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.examples.doc.Author;

/**
 *
 * @author John Grosh (jagrosh)
 */
@CommandInfo(
    name = {"Ping", "Pong"},
    description = "Checks the bot's latency"
)
@Author("John Grosh (jagrosh)")
public class PingCmd extends Command {

    public PingCmd()
    {
        this.name = "ping";
        this.help = "뮤직 모듈의 반응 속도를 보여드려요!";
        this.guildOnly = false;
        this.aliases = new String[]{"ㅔㅑㅜㅎ"};
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply("핑 측정 중: ...", m -> {
            long ping = event.getMessage().getCreationTime().until(m.getCreationTime(), ChronoUnit.MILLIS);
            m.editMessage("퐁! "+event.getSelfUser().getName() + "뮤직 모듈의 핑: " + ping  + "ms | 웹소켓: " + event.getJDA().getPing() + "ms").queue();
        });
    }

}
