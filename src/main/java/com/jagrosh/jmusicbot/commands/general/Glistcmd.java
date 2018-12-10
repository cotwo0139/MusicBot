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

import com.sangoon.builder.*;
import java.awt.Color;
import java.util.concurrent.TimeUnit;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.doc.standard.Error;
import com.jagrosh.jdautilities.doc.standard.RequiredPermissions;
import com.jagrosh.jdautilities.examples.doc.Author;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.exceptions.PermissionException;

/**
 *
 * @author John Grosh (jagrosh)
 */

@Error(
    value = "If arguments are provided, but they are not an integer.",
    response = "[PageNumber] is not a valid integer!"
)
@RequiredPermissions({Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION})

public class Glistcmd extends Command {

    private final Paginator.Builder pbuilder;
    public Glistcmd(EventWaiter waiter)
    {
        this.name = "guildlist";
        this.aliases = new String[]{"길드들","서버리스트","혀ㅑㅣ이ㅑㄴㅅ","tjqjfltmxm","rlfememf"};
        this.help = "봇이 있는 길드의 리스트를 보여드려요!";
        this.arguments = "[페이지 번호]";
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION};
        this.guildOnly = false;
        this.ownerCommand = false;
        
        pbuilder = new Paginator.Builder().setColumns(1)
                .setItemsPerPage(10)
                .showPageNumbers(true)
                .waitOnSinglePage(false)
                .useNumberedItems(false)
                .setFinalAction(m -> {
                    try {
                        m.clearReactions().queue();
                    } catch(PermissionException ex) {
                        m.delete().queue();
                    }
                })
                .setEventWaiter(waiter)
                .setTimeout(1, TimeUnit.MINUTES);
    }

    @Override
    protected void execute(CommandEvent event) {
        int page = 1;
        if(!event.getArgs().isEmpty())
        {
            try
            {
                page = Integer.parseInt(event.getArgs());
            }
            catch(NumberFormatException e)
            {
                event.reply(event.getClient().getError()+" `"+event.getArgs()+"` 는 올바른 정수가 아니에요!");
                return;
            }
        }
        pbuilder.clearItems();
        event.getJDA().getGuilds().stream()
                .map(g -> "**"+g.getName()+"** (ID:"+g.getId()+") ~ "+g.getMembers().size()+" 멤버들")
                .forEach(pbuilder::addItems);
        Paginator p = pbuilder.setColor(event.isFromType(ChannelType.TEXT) ? event.getSelfMember().getColor() : Color.black)
                .setText(event.getClient().getSuccess()+" **"+event.getSelfUser().getName()+"** 가 연결되있는 길드들"
                        +(event.getJDA().getShardInfo()==null ? ":" : "(샤드 ID "+event.getJDA().getShardInfo().getShardId()+"):"))
                .setUsers(event.getAuthor())
                .build();
        p.paginate(event.getChannel(), page);
    }
    
}
