
import cc.kave.commons.model.events.ActivityEvent;
import cc.kave.commons.model.events.CommandEvent;
import cc.kave.commons.model.events.IDEEvent;
import cc.kave.commons.model.events.InfoEvent;
import cc.kave.commons.model.events.NavigationEvent;
import cc.kave.commons.model.events.SystemEvent;
import cc.kave.commons.model.events.completionevents.CompletionEvent;
import cc.kave.commons.model.events.testrunevents.TestRunEvent;
import cc.kave.commons.model.events.userprofiles.UserProfileEvent;
import cc.kave.commons.model.events.versioncontrolevents.VersionControlEvent;
import cc.kave.commons.model.events.visualstudio.BuildEvent;
import cc.kave.commons.model.events.visualstudio.DebuggerEvent;
import cc.kave.commons.model.events.visualstudio.DocumentEvent;
import cc.kave.commons.model.events.visualstudio.EditEvent;
import cc.kave.commons.model.events.visualstudio.FindEvent;
import cc.kave.commons.model.events.visualstudio.IDEStateEvent;
import cc.kave.commons.model.events.visualstudio.SolutionEvent;
import cc.kave.commons.model.events.visualstudio.WindowEvent;
import java.util.HashSet;

/**
 * Class used to match an IDEEvent to a matching string
 * 
 */
public class EventMatcher {
    public HashSet<String> commandTypes = new HashSet<>();
    public HashSet<String> notKnown = new HashSet<>();
    
    /**
     * Matches an IDDEvent to a corresponding event label as a string
     * @param ideEvent the event to match against
     * @param complexCommands true if commands are to be sub typed and false otherwise
     * @return a string representation of the event
     */
    public String matchEventToString(IDEEvent ideEvent, EventGranularity eventGran) {   
        if (ideEvent instanceof ActivityEvent) { 
            return "Activity Event";
        } else if (ideEvent instanceof CompletionEvent) {
            return "Completion Event";
        } else if (ideEvent instanceof CommandEvent) {
            String commandId = ((CommandEvent)ideEvent).getCommandId();
            if(eventGran == EventGranularity.HIGH){
                if(commandId.contains("JetBrains")){
                    return "Com JetBrains";
                } else if(commandId.contains("ObjectBrowsingScope")){
                    return "Com OBS";
                } else if(commandId.contains("Debug")){
                    return "Com debug";
                } else if(commandId.equals("Close")){
                    return "Com close";
                } else if(commandId.contains("TextControl")){
                    return "Com textcontrol";
                } else if(commandId.contains("Edit")){
                    return "Com edit";
                } else if(commandId.contains("Tools")){
                    return "Com tools";
                } else if(commandId.contains("File")){
                    return "Com file";
                } else if(commandId.contains("View")){
                    return "Com view";
                } else if(commandId.contains("Help")){
                    return "Com help";
                } else if(commandId.contains("Build")){
                    return "Com build";
                } else if(commandId.contains("Project")){
                    return "Com project";
                } else if(commandId.contains("Completion")){
                    return "Com completion";
                }else if(commandId.contains("Window")){
                    return "Com window";
                }else if(commandId.contains("ChooseLanguage")){
                    return "Com choose language";
                }else if(commandId.contains("Team")){
                    return "Com team";
                }else if(commandId.contains("ArchitectureDesigner")){
                    return "Com architectureDesigner";
                }else if(commandId.contains("Refactorings")){
                    return "Com refactorings";
                }else if(commandId.contains("SolutionExplorer")){
                    return "Com solution explorer";
                }else if(commandId.contains("UnitTestSession")){
                    return "Com unit test session";
                }else if(commandId.contains("TestExplorer")){
                    return "Com test explorer";
                }else if(commandId.contains("TreeModelBrowser")){
                    return "Com tree model browser";
                }else if(commandId.contains("TypeHierarchy")){
                    return "Com type hierarchy";
                }else if(commandId.contains("ContinuousTesting")){
                    return "Com continuous testing";
                }else if(commandId.contains("NuGet")){
                    return "Com NuGet";
                }else if(commandId.contains("FindUsages")){
                    return "Com find usages";
                }else if(commandId.contains("KaVE")){
                    return "Com KaVE";
                }else if(commandId.contains("SQL")){
                    return "Com SQL";
                }else if(commandId.contains("UnitTest")){
                    return "Com unit test";
                }else if(commandId.contains("Generate")){
                    return "Com generate";
                }else if(commandId.contains("OtherContextMenus")){
                    return "Com other context menues";
                }else if(commandId.contains("ArchitectureContextMenus")){
                    return "Com architecture context menues";
                }else if(commandId.contains("Supercharger")){
                    return "Com supercharger";
                }else if(commandId.contains("BulbAction")){
                    return "Com bulb action";
                }else if(commandId.contains("CodeMaid")){
                    return "Com code maid";
                }else if(commandId.contains("Bookmarks")){
                    return "Com bookmarks";
                }else if(commandId.contains("VisualSVN")){
                    return "Com visual svn";
                }else if(commandId.contains("ViEmu")){
                    return "Com ViEmu";
                }else if(commandId.contains("Mercurial")){
                    return "Com mercurial";
                }else if(commandId.contains("NCrunch")){
                    return "Com NCrunch";
                }else if(commandId.contains("Resources")){
                    return "Com resources";
                }else if(commandId.contains("Save")){
                    return "Com save";
                }else {
                    if(!notKnown.contains(commandId)){
                        //System.out.println(commandId);
                        notKnown.add(commandId);
                    }
                    return "Com unknown";
                }
            } else {
                return commandId;
            }
        }else if (ideEvent instanceof BuildEvent) {
            BuildEvent event = (BuildEvent) ideEvent;
            switch (event.Action) {
                case "vsBuildActionRebuildAll":
                    return "Build event rebuild all";
                case "vsBuildActionClean":
                    return "Build event clean";
                case "vsBuildActionBuild":
                    return "Build event build";
                default:
                    return "Build event unknown";
            }
        }else if (ideEvent instanceof DebuggerEvent) {
            DebuggerEvent event = (DebuggerEvent) ideEvent;
            if(null != event.Mode)switch (event.Mode) {
                case Design:
                    return "Debugger design";
                case Break:
                    return "Debugger break";
                case ExceptionNotHandled:
                    return "Debugger exception not handles";
                case ExceptionThrown:
                    return "Debugger exception thrown";
                case Run:
                    return "Debugger run";
                default:
                    return "Debugger event";
            }
        }else if (ideEvent instanceof DocumentEvent) {
            DocumentEvent event = (DocumentEvent)ideEvent;
            if(null != event.Action)switch (event.Action) {
                case Closing:
                    return "Document close";
                case Opened:
                    return "Document opened";
                case Saved:
                    return "Document saved";
                default:
                    return "Document event";
            }

        }else if (ideEvent instanceof EditEvent) {
            EditEvent event = (EditEvent) ideEvent;
            if(event.NumberOfChanges < 5){
                return "Edit event less than 5 changes";
            } else if(event.NumberOfChanges < 10){
                return "Edit event 5 to 9 changes";
            } else {
                return "Edit event 10 or more changes";
            }
        }else if (ideEvent instanceof FindEvent) {
            FindEvent event = (FindEvent) ideEvent;
            if(event.Cancelled){
                return "Find cancelled";
            } else {
                return "Find complete";
            }
        }else if (ideEvent instanceof IDEStateEvent) {
            IDEStateEvent event = (IDEStateEvent) ideEvent;
            if(null != event.IDELifecyclePhase)switch (event.IDELifecyclePhase) {
                case Runtime:
                    return "IDE state runtime";
                case Shutdown:
                    return "IDE state shutdown";
                case Startup:
                    return "IDE state startup";
                default:
                    return "IDE state unknown";
            }
        }else if (ideEvent instanceof SolutionEvent) {
            SolutionEvent event = (SolutionEvent) ideEvent;
            if(null != event.Action)switch (event.Action) {
                case AddProject:
                    return "Solution add project";
                case AddProjectItem:
                    return "Solution add project item";
                case AddSolutionItem:
                    return "Solution add solution item";
                case CloseSolution:
                    return "Solution close solution";
                case OpenSolution:
                    return "Solution open solution";
                case RemoveProject:
                    return "Solution remove project";
                case RemoveProjectItem:
                    return "Solution remove project item";
                case RemoveSolutionItem:
                    return "Solution remove solution item";
                case RenameProject:
                    return "Solution rename project";
                case RenameProjectItem:
                    return "Solution rename project item";
                case RenameSolution:
                    return "Solution rename solution";
                case RenameSolutionItem:
                    return "Solution rename solution item";
                default:
                    return "Solution unknown";
            }
        }else if (ideEvent instanceof WindowEvent) {
            WindowEvent event = (WindowEvent) ideEvent;
            if(null != event.Action)switch (event.Action) {
                case Activate:
                    return "Window event activate";
                case Close:
                    return "Window event close";
                case Create:
                    return "Window event create";
                case Deactivate:
                    return "Window event deactivate";
                case Move:
                    return "Window event move";
                default:
                    return "Window event unknown";
            }

        }else if (ideEvent instanceof VersionControlEvent){
            return "Version control event";
        }else if (ideEvent instanceof UserProfileEvent) {
            return ""; // Don't include these
        }else if (ideEvent instanceof NavigationEvent) {
            NavigationEvent event = (NavigationEvent) ideEvent;
            if(null != event.TypeOfNavigation)switch (event.TypeOfNavigation) {
                case Click:
                    return "Navigation event click";
                case CtrlClick:
                    return "Navigation event ctrl click";
                case Keyboard:
                    return "Navigation event keyboard";
                default:
                    return "Navigation event unknown";
            }

        }else if (ideEvent instanceof SystemEvent) {
            SystemEvent event = (SystemEvent) ideEvent;
            if(null != event.Type)switch (event.Type) {
                case Lock:
                    return "System event lock";
                case RemoteConnect:
                    return "System event remote connect";
                case RemoteDisconnect:
                    return "System event remote disconnect";
                case Resume:
                    return "System event resume";
                case Suspend:
                    return "System event suspend";
                case Unknown:
                    return "System event unknown";
                case Unlock:
                    return "System event unlock";
                default:
                    return "System event unknown";
            }
        }else if (ideEvent instanceof TestRunEvent) {
            return "Test run event";
        }else if (ideEvent instanceof InfoEvent) { // not needed
            return "";
        }else {
            return "Unknown event type";
        }
        return "";
    }
}
