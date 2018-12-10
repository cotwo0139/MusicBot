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
import net.dv8tion.jda.core.OnlineStatus;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class SetstatusCmd extends OwnerCommand
{
    public SetstatusCmd()
    {
        this.name = "setstatus";
        this.help = "봇의 상태를 설정합니다";
        this.arguments = "<ONLINE | IDLE | DND | INVISIBLE>";
        this.guildOnly = false;
    }
    
    @Override
    protected void execute(CommandEvent event) 
    {
        try {
            OnlineStatus status = OnlineStatus.fromKey(event.getArgs());
            if(status==OnlineStatus.UNKNOWN)
            {
                event.replyError("`ONLINE`, `IDLE`, `DND`, `INVISIBLE` 중 1개를 포함해주세요!");
            }
            else
            {
                event.getJDA().getPresence().setStatus(status);
                event.replySuccess("상태가 `"+status.getKey().toUpperCase()+"` 로 설정되었어요!");
            }
        } catch(Exception e) {
            event.reply(event.getClient().getError()+" 상태가 설정되지 않았어요!");
        }
    }
}
