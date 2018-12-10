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

import java.io.IOException;
import java.io.InputStream;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.commands.OwnerCommand;
import com.jagrosh.jmusicbot.utils.OtherUtil;
import net.dv8tion.jda.core.entities.Icon;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class SetavatarCmd extends OwnerCommand 
{
    public SetavatarCmd()
    {
        this.name = "setavatar";
        this.help = "봇의 아바타를 설정해요!";
        this.arguments = "<주소>";
        this.guildOnly = false;
    }
    
    @Override
    protected void execute(CommandEvent event) 
    {
        String url;
        if(event.getArgs().isEmpty())
            if(!event.getMessage().getAttachments().isEmpty() && event.getMessage().getAttachments().get(0).isImage())
                url = event.getMessage().getAttachments().get(0).getUrl();
            else
                url = null;
        else
            url = event.getArgs();
        InputStream s = OtherUtil.imageFromUrl(url);
        if(s==null)
        {
            event.reply(event.getClient().getError()+" 존재하지 않거나 잘못된 주소에요!");
        }
        else
        {
            try {
            event.getSelfUser().getManager().setAvatar(Icon.from(s)).queue(
                    v -> event.reply(event.getClient().getSuccess()+" 성공적으로 아바타를 바꾸었어요!"), 
                    t -> event.reply(event.getClient().getError()+" 아바타 설정에 실패했어요!"));
            } catch(IOException e) {
                event.reply(event.getClient().getError()+" 제공된 주소를 로드할수 없어요");
            }
        }
    }
}
