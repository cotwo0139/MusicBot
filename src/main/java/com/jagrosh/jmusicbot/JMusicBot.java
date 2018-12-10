/*
 * Copyright 2016 John Grosh (jagrosh).
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
package com.jagrosh.jmusicbot;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.examples.command.*;
import com.jagrosh.jmusicbot.commands.admin.*;
import com.jagrosh.jmusicbot.commands.dj.*;
import com.jagrosh.jmusicbot.commands.general.*;
import com.jagrosh.jmusicbot.commands.music.*;
import com.jagrosh.jmusicbot.commands.owner.*;
import com.jagrosh.jmusicbot.entities.Prompt;
import com.jagrosh.jmusicbot.gui.GUI;
import com.jagrosh.jmusicbot.settings.SettingsManager;
import com.jagrosh.jmusicbot.utils.OtherUtil;
import java.awt.Color;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author John Grosh (jagrosh)
 */
public class JMusicBot 
{
    public final static String PLAY_EMOJI  = "\u25B6";
    public final static String PAUSE_EMOJI = "\u23F8";
    public final static String STOP_EMOJI  = "\u23F9";
    public final static Permission[] RECOMMENDED_PERMS = new Permission[]{Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION,
                                Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_MANAGE, Permission.MESSAGE_EXT_EMOJI,
                                Permission.MANAGE_CHANNEL, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.NICKNAME_CHANGE};
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        // startup log
        Logger log = LoggerFactory.getLogger("Startup");
        
        // create prompt to handle startup
        Prompt prompt = new Prompt("JMusicBot", "nogui 모드로 전환합니다. -Dnogui = true 플래그를 포함시켜 nogui 모드에서 수동으로 시작할 수 있습니다.", 
                "true".equalsIgnoreCase(System.getProperty("nogui", "false")));
        
        // check deprecated nogui mode (new way of setting it is -Dnogui=true)
        for(String arg: args)
            if("-nogui".equalsIgnoreCase(arg))
            {
                prompt.alert(Prompt.Level.WARNING, "GUI", "-nogui 플래그는 더 이상 사용되지 않습니다."
                        + "jar 이름 앞에 -Dnogui = true 플래그를 사용하십시오. 예 : java -jar -Dnogui=true JMusicBot.jar");
                break;
            }
        
        // get and check latest version
        String version = OtherUtil.checkVersion(prompt);
        
        // load config
        BotConfig config = new BotConfig(prompt);
        config.load();
        if(!config.isValid())
            return;
        
        // set up the listener
        EventWaiter waiter = new EventWaiter();
        SettingsManager settings = new SettingsManager();
        Bot bot = new Bot(waiter, config, settings);
        
        AboutCommand aboutCommand = new AboutCommand(Color.BLUE.brighter(),
                                "이 뮤직 봇은 한글화 되었어요! 원 제작 소스는 [클릭해주세요](https://github.com/jagrosh/MusicBot) (v"+version+")",
                                new String[]{"높은 품질의 음악 재생 기능", "FairQueue™ 기술", "다양한 기능들"},
                                RECOMMENDED_PERMS);
        aboutCommand.setIsAuthor(false);
        aboutCommand.setReplacementCharacter("\uD83C\uDFB6");
        
        // set up the command client
        CommandClientBuilder cb = new CommandClientBuilder()
                .setPrefix(config.getPrefix())
                .setAlternativePrefix(config.getAltPrefix())
                .setOwnerId(Long.toString(config.getOwnerId()))
                .setEmojis(config.getSuccess(), config.getWarning(), config.getError())
                .setHelpWord(config.getHelp())
                .setLinkedCacheSize(200)
                .setGuildSettingsManager(settings)
                .addCommands(aboutCommand,
                        new PingCommand(),
                        new SettingsCmd(),
                        
                        new LyricsCmd(bot),
                        new NowplayingCmd(bot),
                        new PlayCmd(bot, config.getLoading()),
                        new PlaylistsCmd(bot),
                        new QueueCmd(bot),
                        new RemoveCmd(bot),
                        new SearchCmd(bot, config.getSearching()),
                        new SCSearchCmd(bot, config.getSearching()),
                        new ShuffleCmd(bot),
                        new SkipCmd(bot),
                        
                        new ForceskipCmd(bot),
                        new PauseCmd(bot),
                        new PlaynextCmd(bot, config.getLoading()),
                        new RepeatCmd(bot),
                        new SkiptoCmd(bot),
                        new StopCmd(bot),
                        new VolumeCmd(bot),
                        
                        new SetdjCmd(),
                        new SettcCmd(),
                        new SetvcCmd(),
                        
                        new AutoplaylistCmd(bot),
                        new PlaylistCmd(bot),
                        new SetavatarCmd(),
                        new SetgameCmd(),
                        new SetnameCmd(),
                        new SetstatusCmd(),
                        new ShutdownCmd(bot)
                );
        if(config.useEval())
            cb.addCommand(new EvalCmd(bot));
        boolean nogame = false;
        if(config.getStatus()!=OnlineStatus.UNKNOWN)
            cb.setStatus(config.getStatus());
        if(config.getGame()==null)
            cb.useDefaultGame();
        else if(config.getGame().getName().equalsIgnoreCase("none"))
        {
            cb.setGame(null);
            nogame = true;
        }
        else
            cb.setGame(config.getGame());
        CommandClient client = cb.build();
        
        if(!prompt.isNoGUI())
        {
            try 
            {
                GUI gui = new GUI(bot);
                bot.setGUI(gui);
                gui.init();
            } 
            catch(Exception e) 
            {
                log.error("GUI 를 실행하지 못했습니다. 혹시"
                        + "서버용 OS 에서 실행하고 있는지 확인해주세요."
                        + "서버에서 사용하실 때에는 -Dnogui=true 플래그를 사용해 주세요.");
            }
        }
        
        log.info("콘피그를 "+config.getConfigLocation()+" 에서 불러오고 있습니다...");
        
        // attempt to log in and start
        try
        {
            JDA jda = new JDABuilder(AccountType.BOT)
                    .setToken(config.getToken())
                    .setAudioEnabled(true)
                    .setGame(nogame ? null : Game.playing("로딩중..."))
                    .setStatus(config.getStatus()==OnlineStatus.INVISIBLE||config.getStatus()==OnlineStatus.OFFLINE ? OnlineStatus.INVISIBLE : OnlineStatus.DO_NOT_DISTURB)
                    .addEventListener(client, waiter, new Listener(bot))
                    .setBulkDeleteSplittingEnabled(true)
                    .build();
            bot.setJDA(jda);
        }
        catch (LoginException ex)
        {
            log.error(ex+"\nconfig.txt 파일을 수정해 주세요"
                    + "그리고 정확한 토큰을 입력해주세요"
                    + "정확한 \"Token\" 을 입력해주세요 (\"Secret\" 이 아닙니다!!)");
            System.exit(1);
        }
        catch(IllegalArgumentException ex)
        {
            log.error("다음의 설정이 존재하지 않습니다: "+ex);
            System.exit(1);
        }
    }
}
