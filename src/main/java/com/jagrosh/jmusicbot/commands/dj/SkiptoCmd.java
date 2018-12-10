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
package com.jagrosh.jmusicbot.commands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.commands.DJCommand;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class SkiptoCmd extends DJCommand 
{
    public SkiptoCmd(Bot bot)
    {
        super(bot);
        this.name = "skipto";
        this.help = "선택한 곡으로 건너뛰어요!";
        this.arguments = "<위치>";
        this.aliases = new String[]{"jumpto","점프","나ㅑㅔ새","wjavm","tjsxorrjssjEnlrl","선택건너뛰기"};
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event) 
    {
        int index = 0;
        try
        {
            index = Integer.parseInt(event.getArgs());
        }
        catch(NumberFormatException e)
        {
            event.reply(event.getClient().getError()+" `"+event.getArgs()+"` 는 정수가 아니에요!");
            return;
        }
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        if(index<1 || index>handler.getQueue().size())
        {
            event.reply(event.getClient().getError()+" 위지는 정수이거나 1부터 "+handler.getQueue().size()+"까지만 가능해요!");
            return;
        }
        handler.getQueue().skip(index-1);
        event.reply(event.getClient().getSuccess()+" **"+handler.getQueue().get(0).getTrack().getInfo().title+"** 곡으로 건너뛰었어요!");
        handler.getPlayer().stopTrack();
    }
}
