package sessionj;

import java.io.*;
import java.util.*;

import polyglot.ast.*;
import polyglot.frontend.*;
import polyglot.frontend.goals.*;
import polyglot.lex.Lexer;
import polyglot.main.*;
import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;

import sessionj.parse.*;
import sessionj.ast.*;
import sessionj.types.*;
import sessionj.visit.*;
import sessionj.visit.noalias.*;

/**
 * Extension information for sessionj extension.
 */
public class ExtensionInfo extends polyglot.frontend.JLExtensionInfo {
	static {
	    // force Topics to load
	    Topics t = new Topics();
	}
	
	public String defaultFileExtension() {
	    return "sj";
	}
	
	public String compilerName() {
	    return "sessionjc";
	}
	
	public Parser parser(Reader reader, FileSource source, ErrorQueue eq) {
	    Lexer lexer = new Lexer_c(reader, source, eq);
	    Grm grm = new Grm(lexer, ts, nf, eq);
	    return new CupParser(grm, source, eq);
	}
	
	protected NodeFactory createNodeFactory() {
	    return new SJNodeFactory_c();
	}
	
	protected TypeSystem createTypeSystem() {
	    return new SJTypeSystem_c();
	}
	
	// Must override this method to specify the new compiler pass schedule.
	public Scheduler createScheduler()
	{
		return new SJScheduler(this);
	}
	
	static class SJScheduler extends JLScheduler
	{
		SJScheduler(ExtensionInfo extInfo)
		{
			super(extInfo);
		}
	
		// TypesInitialized phase (post Parsed, pre TypeChecked).
		/*public Goal SJThreadPreParsing(final Job job)
		{
			TypeSystem ts = job.extensionInfo().typeSystem();
			NodeFactory nf = job.extensionInfo().nodeFactory();
			Goal g = internGoal(new VisitorGoal(job, new SJThreadPreParser(job, ts, nf))
			{
				public Collection prerequisiteGoals(Scheduler scheduler)
				{
					List l = new ArrayList();
					
					l.add(scheduler.Parsed(job)); // Copied from inside polyglot.frontend.goals.TypesInitialized (i.e. the next pass).
					
					return l;
				}
			});
			return g;
		}

    public Goal TypesInitialized(Job job)
    {
			TypeSystem ts = extInfo.typeSystem();
			NodeFactory nf = extInfo.nodeFactory();
			Goal g = internGoal(new VisitorGoal(job, new TypeBuilder(job, ts, nf))
			{
				public Collection prerequisiteGoals(Scheduler scheduler)
				{
					List l = new ArrayList();
					
					l.addAll(super.prerequisiteGoals(scheduler));
					l.add(SJThreadPreParsing(job));
					
					return l;
	    	}
			});
			return g;
    }*/
		
		// ReachabilityChecked phase (post TypeChecked).
		public Goal SJCreateOperationParsing(final Job job)
		{
			TypeSystem ts = job.extensionInfo().typeSystem();
			NodeFactory nf = job.extensionInfo().nodeFactory();
	
			Goal g = internGoal(new VisitorGoal(job, new SJCreateOperationParser(job,
			    ts, nf))
				{
					public Collection prerequisiteGoals(Scheduler scheduler)
					{
						List l = new ArrayList();
		
						// l.addAll(super.prerequisiteGoals(scheduler));
						l.add(scheduler.TypeChecked(job)); // Copied from ReachabilityChecked pass.
						l.add(scheduler.ConstantsChecked(job));
		
						return l;
					}
				}
			);
	
			return g;
		}
	
		/*public Goal PolyglotToSJBarrier(final Job job)
		{
			Goal g = internGoal(new Barrier(this)
				{
					public Goal goalForJob(Job j) // Brings all jobs (compilation units, i.e. source files) up this barrier (i.e. the first SJ noalias type building pass is completed) before proceeding with the subsequent passes. 
					{
						return ((SJScheduler) scheduler).SJCreateOperationParsing(j);
					}
				}
			);
	
			return g;
		}*/
		
		public Goal SJVariableParsing(final Job job)
		{
			TypeSystem ts = job.extensionInfo().typeSystem();
			NodeFactory nf = job.extensionInfo().nodeFactory();
	
			Goal g = internGoal(new VisitorGoal(job, new SJVariableParser(job,
			    ts, nf))
				{
					public Collection prerequisiteGoals(Scheduler scheduler)
					{
						List l = new ArrayList();
		
						// l.addAll(super.prerequisiteGoals(scheduler));
						l.add(SJCreateOperationParsing(job));
						//l.add(PolyglotToSJBarrier(job));
		
						return l;
					}
				}
			);
	
			return g;
		}
	
		public Goal SJSessionTryDisambiguation(final Job job)
		{
			TypeSystem ts = job.extensionInfo().typeSystem();
			NodeFactory nf = job.extensionInfo().nodeFactory();
	
			Goal g = internGoal(new VisitorGoal(job, new SJSessionTryDisambiguator(job,
			    ts, nf))
				{
					public Collection prerequisiteGoals(Scheduler scheduler)
					{
						List l = new ArrayList();
		
						// l.addAll(super.prerequisiteGoals(scheduler));
						l.add(SJVariableParsing(job));
		
						return l;
					}
				}
			);
	
			return g;
		}	
		
		public Goal SJThreadParsing(final Job job)
		{
			TypeSystem ts = job.extensionInfo().typeSystem();
			NodeFactory nf = job.extensionInfo().nodeFactory();
	
			Goal g = internGoal(new VisitorGoal(job, new SJThreadParser(job,
			    ts, nf))
				{
					public Collection prerequisiteGoals(Scheduler scheduler)
					{
						List l = new ArrayList();
		
						// l.addAll(super.prerequisiteGoals(scheduler));
						l.add(SJSessionTryDisambiguation(job));
		
						return l;
					}
				}
			);
	
			return g;
		}
		
		public Goal SJThreadParsingBarrier(final Job job) 
		{
			Goal g = internGoal(new Barrier(this)
				{
					public Goal goalForJob(Job j)  
					{
						return ((SJScheduler) scheduler).SJThreadParsing(j);
					}
				}
			);
	
			return g;
		}
		
		public Goal SJChannelOperationParsing(final Job job)
		{
			TypeSystem ts = job.extensionInfo().typeSystem();
			NodeFactory nf = job.extensionInfo().nodeFactory();
	
			Goal g = internGoal(new VisitorGoal(job, new SJChannelOperationParser(job,
			    ts, nf))
				{
					public Collection prerequisiteGoals(Scheduler scheduler)
					{
						List l = new ArrayList();
		
						// l.addAll(super.prerequisiteGoals(scheduler));
						//l.add(SJSessionTryDisambiguation(job));
						//l.add(SJThreadParsing(job));
						l.add(SJThreadParsingBarrier(job));
						
						return l;
					}
				}
			);
	
			return g;
		}	
		
		public Goal SJServerOperationParsing(final Job job)
		{
			TypeSystem ts = job.extensionInfo().typeSystem();
			NodeFactory nf = job.extensionInfo().nodeFactory();
	
			Goal g = internGoal(new VisitorGoal(job, new SJServerOperationParser(job,
			    ts, nf))
				{
					public Collection prerequisiteGoals(Scheduler scheduler)
					{
						List l = new ArrayList();
		
						// l.addAll(super.prerequisiteGoals(scheduler));
						l.add(SJChannelOperationParsing(job));
		
						return l;
					}
				}
			);
	
			return g;
		}	
		
		public Goal SJSessionOperationParsing(final Job job)
		{
			TypeSystem ts = job.extensionInfo().typeSystem();
			NodeFactory nf = job.extensionInfo().nodeFactory();
	
			Goal g = internGoal(new VisitorGoal(job, new SJSessionOperationParser(job,
			    ts, nf))
				{
					public Collection prerequisiteGoals(Scheduler scheduler)
					{
						List l = new ArrayList();
		
						// l.addAll(super.prerequisiteGoals(scheduler));
						l.add(SJServerOperationParsing(job));
		
						return l;
					}
				}
			);
	
			return g;
		}		
		
		/*public Goal SJNoAliasTypeBuilding1(final Job job)
		{
			TypeSystem ts = job.extensionInfo().typeSystem();
			NodeFactory nf = job.extensionInfo().nodeFactory();
	
			Goal g = internGoal(new VisitorGoal(job, new SJNoAliasTypeBuilder1(job,
			    ts, nf))
				{
					public Collection prerequisiteGoals(Scheduler scheduler)
					{
						List l = new ArrayList();
		
						// l.addAll(super.prerequisiteGoals(scheduler));
						l.add(SJSessionOperationParsing(job));
		
						return l;
					}
				}
			);
	
			return g;
		}*/		
		
		/*public Goal SJNoAliasTypeBuildingBarrier1(final Job job)
		{
			Goal g = internGoal(new Barrier(this)
				{
					public Goal goalForJob(Job j) // Brings all jobs (compilation units, i.e. source files) up this barrier (i.e. the first SJ noalias type building pass is completed) before proceeding with the subsequent passes. 
					{
						return ((SJScheduler) scheduler).SJNoAliasTypeBuilding1(j);
					}
				}
			);
	
			return g;
		}*/
		
		public Goal SJNoAliasTypeBuilding(final Job job)
		{
			TypeSystem ts = job.extensionInfo().typeSystem();
			NodeFactory nf = job.extensionInfo().nodeFactory();
	
			Goal g = internGoal(new VisitorGoal(job, new SJNoAliasTypeBuilder(job,
			    ts, nf))
				{
					public Collection prerequisiteGoals(Scheduler scheduler)
					{
						List l = new ArrayList();
		
						// l.addAll(super.prerequisiteGoals(scheduler));
						//l.add(SJSessionOperationParsing(job));
						l.add(SJSessionOperationParsing(job));
						//l.add(SJNoAliasTypeBuilding1(job));
						//l.add(SJNoAliasTypeBuildingBarrier1(job));
		
						return l;
					}
				}
			);
	
			return g;
		}
	
		public Goal SJNoAliasTypeBuildingBarrier(final Job job)
		{
			Goal g = internGoal(new Barrier(this)
				{
					public Goal goalForJob(Job j) // Brings all jobs (compilation units, i.e. source files) up this barrier (i.e. the first SJ noalias type building pass is completed) before proceeding with the subsequent passes. 
					{
						return ((SJScheduler) scheduler).SJNoAliasTypeBuilding(j);
					}
				}
			);
	
			return g;
		}
		
		public Goal SJNoAliasExprBuilding(final Job job)
		{
			TypeSystem ts = job.extensionInfo().typeSystem();
			NodeFactory nf = job.extensionInfo().nodeFactory();
	
			Goal g = internGoal(new VisitorGoal(job, new SJNoAliasExprBuilder(job,
			    ts, nf))
				{
					public Collection prerequisiteGoals(Scheduler scheduler)
					{
						List l = new ArrayList();
		
						// l.addAll(super.prerequisiteGoals(scheduler));
						l.add(SJNoAliasTypeBuildingBarrier(job));
		
						return l;
					}
				}
			);
	
			return g;
		}

		public Goal SJProtocolDeclTypeBuilding(final Job job)
		{
			TypeSystem ts = job.extensionInfo().typeSystem();
			NodeFactory nf = job.extensionInfo().nodeFactory();
	
			Goal g = internGoal(new VisitorGoal(job, new SJProtocolDeclTypeBuilder(job,
			    ts, nf))
				{
					public Collection prerequisiteGoals(Scheduler scheduler)
					{
						List l = new ArrayList();
		
						// l.addAll(super.prerequisiteGoals(scheduler));
						l.add(SJNoAliasExprBuilding(job));
		
						return l;
					}
				}
			);
	
			return g;
		}
		
		public Goal SJProtocolDeclTypeBuildingBarrier(final Job job)
		{
			Goal g = internGoal(new Barrier(this)
				{
					public Goal goalForJob(Job j) // Brings all jobs (compilation units, i.e. source files) up this barrier (i.e. the first SJ noalias type building pass is completed) before proceeding with the subsequent passes. 
					{
						return ((SJScheduler) scheduler).SJProtocolDeclTypeBuilding(j);
					}
				}
			);
	
			return g;
		}		
	
		public Goal SJSessionMethodTypeBuilding(final Job job)
		{
			TypeSystem ts = job.extensionInfo().typeSystem();
			NodeFactory nf = job.extensionInfo().nodeFactory();
	
			Goal g = internGoal(new VisitorGoal(job, new SJSessionMethodTypeBuilder(job,
			    ts, nf))
				{
					public Collection prerequisiteGoals(Scheduler scheduler)
					{
						List l = new ArrayList();
		
						// l.addAll(super.prerequisiteGoals(scheduler));
						l.add(SJProtocolDeclTypeBuildingBarrier(job));
		
						return l;
					}
				}
			);
	
			return g;
		}
		
		public Goal SJChannelDeclTypeBuilding(final Job job)
		{
			TypeSystem ts = job.extensionInfo().typeSystem();
			NodeFactory nf = job.extensionInfo().nodeFactory();
	
			Goal g = internGoal(new VisitorGoal(job, new SJChannelDeclTypeBuilder(job,
			    ts, nf))
				{
					public Collection prerequisiteGoals(Scheduler scheduler)
					{
						List l = new ArrayList();
		
						// l.addAll(super.prerequisiteGoals(scheduler));
						//l.add(SJProtocolDeclTypeBuildingBarrier(job));
						l.add(SJSessionMethodTypeBuilding(job));
						
						return l;
					}
				}
			);
	
			return g;
		}		
		
		public Goal SJServerDeclTypeBuilding(final Job job)
		{
			TypeSystem ts = job.extensionInfo().typeSystem();
			NodeFactory nf = job.extensionInfo().nodeFactory();
	
			Goal g = internGoal(new VisitorGoal(job, new SJServerDeclTypeBuilder(job,
			    ts, nf))
				{
					public Collection prerequisiteGoals(Scheduler scheduler)
					{
						List l = new ArrayList();
		
						// l.addAll(super.prerequisiteGoals(scheduler));
						l.add(SJChannelDeclTypeBuilding(job));
		
						return l;
					}
				}
			);
	
			return g;
		}				
		
		/*public Goal SJChannelDeclTypeBuildingBarrier(final Job job) // Not needed because channels (and servers) can only be locals.
		{
			Goal g = internGoal(new Barrier(this)
				{
					public Goal goalForJob(Job j) // Brings all jobs (compilation units, i.e. source files) up this barrier (i.e. the first SJ noalias type building pass is completed) before proceeding with the subsequent passes. 
					{
						return ((SJScheduler) scheduler).SJChannelDeclTypeBuilding(j);
					}
				}
			);
	
			return g;
		}*/
	
		public Goal SJSocketDeclTypeBuilding(final Job job)
		{
			TypeSystem ts = job.extensionInfo().typeSystem();
			NodeFactory nf = job.extensionInfo().nodeFactory();
	
			Goal g = internGoal(new VisitorGoal(job, new SJSocketDeclTypeBuilder(job,
			    ts, nf))
				{
					public Collection prerequisiteGoals(Scheduler scheduler)
					{
						List l = new ArrayList();
		
						// l.addAll(super.prerequisiteGoals(scheduler));
						//l.add(SJChannelDeclTypeBuilding(job)); 
						//l.add(SJChannelDeclTypeBuildingBarrier(job)); // Barrier not needed, dealing with locals only.
						l.add(SJServerDeclTypeBuilding(job));
		
						return l;
					}
				}
			);
	
			return g;
		}		
		
		public Goal SJSessionOperationTypeBuilding(final Job job)
		{
			TypeSystem ts = job.extensionInfo().typeSystem();
			NodeFactory nf = job.extensionInfo().nodeFactory();
	
			Goal g = internGoal(new VisitorGoal(job, new SJSessionOperationTypeBuilder(job,
			    ts, nf))
				{
					public Collection prerequisiteGoals(Scheduler scheduler)
					{
						List l = new ArrayList();
		
						// l.addAll(super.prerequisiteGoals(scheduler));
						l.add(SJSocketDeclTypeBuilding(job)); // Barrier not needed, dealing with locals only.
						//l.add(SJChannelDeclTypeBuildingBarrier(job));
		
						return l;
					}
				}
			);
	
			return g;
		}	
		
		public Goal SJTypeBuildingBarrier(final Job job) // Maybe not needed?
		{
			Goal g = internGoal(new Barrier(this)
				{
					public Goal goalForJob(Job j) // Brings all jobs (compilation units, i.e. source files) up this barrier (i.e. the first SJ noalias type building pass is completed) before proceeding with the subsequent passes. 
					{
						return ((SJScheduler) scheduler).SJSessionOperationTypeBuilding(j);
					}
				}
			);
	
			return g;
		}
		
		public Goal SJNoAliasTypeChecking(final Job job)
		{
			TypeSystem ts = job.extensionInfo().typeSystem();
			NodeFactory nf = job.extensionInfo().nodeFactory();
	
			Goal g = internGoal(new VisitorGoal(job, new SJNoAliasTypeChecker(job,
			    ts, nf))
				{
					public Collection prerequisiteGoals(Scheduler scheduler)
					{
						List l = new ArrayList();
		
						// l.addAll(super.prerequisiteGoals(scheduler));
						l.add(SJTypeBuildingBarrier(job));
		
						return l;
					}
				}
			);
	
			return g;
		}
			
		// Maybe put a barrier between these two, so that session type checking can really count on linearity.
		
		public Goal SJSessionTypeChecking(final Job job) // Doing this after noalias type checking means session linearity is already checked.
		{
			TypeSystem ts = job.extensionInfo().typeSystem();
			NodeFactory nf = job.extensionInfo().nodeFactory();
	
			Goal g = internGoal(new VisitorGoal(job, new SJSessionTypeChecker(job,
			    ts, nf))
				{
					public Collection prerequisiteGoals(Scheduler scheduler)
					{
						List l = new ArrayList();
		
						// l.addAll(super.prerequisiteGoals(scheduler));
						l.add(SJNoAliasTypeChecking(job));
		
						return l;
					}
				}
			);
	
			return g;
		}
		
		public Goal ReachabilityChecked(final Job job)
		{
			TypeSystem ts = extInfo.typeSystem();
			NodeFactory nf = extInfo.nodeFactory();
	
			Goal g = internGoal(new ReachabilityChecked(job, ts, nf)
				{
					public Collection prerequisiteGoals(Scheduler scheduler)
					{
						List l = new ArrayList();
		
						l.addAll(super.prerequisiteGoals(scheduler));
						l.add(SJSessionTypeChecking(job));
		
						return l;
					}
				}
			);
	
			return g;
		}
		// End of ReachabilityChecked phase.
	
		// Start of Serialized phase.
		public Goal SJSessionVisiting(final Job job) // All session type has been information built, checked and recorded.
		{
			TypeSystem ts = job.extensionInfo().typeSystem();
			NodeFactory nf = job.extensionInfo().nodeFactory();
	
			Goal g = internGoal(new VisitorGoal(job, new SJSessionVisitor(job,
			    ts, nf))
				{
					public Collection prerequisiteGoals(Scheduler scheduler)
					{
						List l = new ArrayList();
		
						// l.addAll(super.prerequisiteGoals(scheduler));
		        l.add(scheduler.TypeChecked(job));
		        l.add(scheduler.ConstantsChecked(job));
		        l.add(scheduler.ReachabilityChecked(job));
		        l.add(scheduler.ExceptionsChecked(job));
		        l.add(scheduler.ExitPathsChecked(job));
		        l.add(scheduler.InitializationsChecked(job));
		        l.add(scheduler.ConstructorCallsChecked(job));
		        l.add(scheduler.ForwardReferencesChecked(job));													
		
						return l;
					}
				}
			);
	
			return g;
		}		
		
		public Goal SJSendTranslation(final Job job) // Needs to come before noalias translation.
		{
			TypeSystem ts = job.extensionInfo().typeSystem();
			NodeFactory nf = job.extensionInfo().nodeFactory();
	
			Goal g = internGoal(new VisitorGoal(job, new SJSendTranslator(job,
			    ts, nf))
				{
					public Collection prerequisiteGoals(Scheduler scheduler)
					{
						List l = new ArrayList();
		
						// l.addAll(super.prerequisiteGoals(scheduler));
		        /*l.add(scheduler.TypeChecked(job));
		        l.add(scheduler.ConstantsChecked(job));
		        l.add(scheduler.ReachabilityChecked(job));
		        l.add(scheduler.ExceptionsChecked(job));
		        l.add(scheduler.ExitPathsChecked(job));
		        l.add(scheduler.InitializationsChecked(job));
		        l.add(scheduler.ConstructorCallsChecked(job));
		        l.add(scheduler.ForwardReferencesChecked(job));*/
						l.add(SJSessionVisiting(job));
		
						return l;
					}
				}
			);
	
			return g;
		}
		
		/*public Goal SJSessionVisiting2(final Job job) // All session type has been information built, checked and recorded.
		{
			TypeSystem ts = job.extensionInfo().typeSystem();
			NodeFactory nf = job.extensionInfo().nodeFactory();
	
			Goal g = internGoal(new VisitorGoal(job, new SJSessionVisitor(job,
			    ts, nf))
				{
					public Collection prerequisiteGoals(Scheduler scheduler)
					{
						List l = new ArrayList();
		
						// l.addAll(super.prerequisiteGoals(scheduler));
						l.add(SJSendTranslation(job));	
		
						return l;
					}
				}
			);
	
			return g;
		}*/
		
		public Goal SJNoAliasTranslation(final Job job)
		{
			TypeSystem ts = job.extensionInfo().typeSystem();
			NodeFactory nf = job.extensionInfo().nodeFactory();
	
			Goal g = internGoal(new VisitorGoal(job, new SJNoAliasTranslator(job,
			    ts, nf))
				{
					public Collection prerequisiteGoals(Scheduler scheduler)
					{
						List l = new ArrayList();
		
						//l.addAll(super.prerequisiteGoals(scheduler));
						l.add(SJSendTranslation(job));
						//l.add(SJSessionVisiting2(job));
		
						return l;
					}
				}
			);
	
			return g;
		}
	
		public Goal SJProtocolDeclTranslation(final Job job) // Doing this after noalias type checking means session linearity is already checked.
		{
			TypeSystem ts = job.extensionInfo().typeSystem();
			NodeFactory nf = job.extensionInfo().nodeFactory();
	
			Goal g = internGoal(new VisitorGoal(job, new SJProtocolDeclTranslator(job,
			    ts, nf))
				{
					public Collection prerequisiteGoals(Scheduler scheduler)
					{
						List l = new ArrayList();
		
						// l.addAll(super.prerequisiteGoals(scheduler));
						l.add(SJNoAliasTranslation(job));
		
						return l;
					}
				}
			);
			
			return g;
		}
		
		public Goal SJSessionTryTranslation(final Job job) // Doing this after noalias type checking means session linearity is already checked.
		{
			TypeSystem ts = job.extensionInfo().typeSystem();
			NodeFactory nf = job.extensionInfo().nodeFactory();
	
			Goal g = internGoal(new VisitorGoal(job, new SJSessionTryTranslator(job,
			    ts, nf))
				{
					public Collection prerequisiteGoals(Scheduler scheduler)
					{
						List l = new ArrayList();
		
						// l.addAll(super.prerequisiteGoals(scheduler));
						//l.add(SJNoAliasTranslation(job));
						l.add(SJProtocolDeclTranslation(job));
		
						return l;
					}
				}
			);
	
			return g;
		}		

		public Goal SJHigherOrderTranslation(final Job job) // Doing this after noalias type checking means session linearity is already checked.
		{
			TypeSystem ts = job.extensionInfo().typeSystem();
			NodeFactory nf = job.extensionInfo().nodeFactory();
	
			Goal g = internGoal(new VisitorGoal(job, new SJHigherOrderTranslator(job,
			    ts, nf))
				{
					public Collection prerequisiteGoals(Scheduler scheduler)
					{
						List l = new ArrayList();
		
						// l.addAll(super.prerequisiteGoals(scheduler));
						//l.add(SJProtocolDeclTranslation(job));
						l.add(SJSessionTryTranslation(job));
		
						return l;
					}
				}
			);
	
			return g;
		}
		
		public Goal SJUnicastOptimisation(final Job job) // Currently need to do this before compound operation translation, because the latter destroys the inbranch node and we don't have explicit nodes for the compound socket operations. 
		{
			TypeSystem ts = job.extensionInfo().typeSystem();
			NodeFactory nf = job.extensionInfo().nodeFactory();
	
			Goal g = internGoal(new VisitorGoal(job, new SJUnicastOptimiser(job,
			    ts, nf))
				{
					public Collection prerequisiteGoals(Scheduler scheduler)
					{
						List l = new ArrayList();
		
						// l.addAll(super.prerequisiteGoals(scheduler));
						l.add(SJHigherOrderTranslation(job));
		
						return l;
					}
				}
			);
	
			return g;
		}
		
		public Goal SJCompoundOperationTranslation(final Job job) 
		{
			TypeSystem ts = job.extensionInfo().typeSystem();
			NodeFactory nf = job.extensionInfo().nodeFactory();
	
			Goal g = internGoal(new VisitorGoal(job, new SJCompoundOperationTranslator(job,
			    ts, nf))
				{
					public Collection prerequisiteGoals(Scheduler scheduler)
					{
						List l = new ArrayList();
		
						// l.addAll(super.prerequisiteGoals(scheduler));
						//l.add(SJNoAliasTranslation(job));
						//l.add(SJProtocolDeclTranslation(job));
						//l.add(SJSessionTryTranslation(job));
						//l.add(SJHigherOrderTranslation(job));
						l.add(SJUnicastOptimisation(job));
		
						return l;
					}
				}
			);
	
			return g;
		}		
		
		public Goal Serialized(final Job job)
		{
			/*TypeSystem ts = extInfo.typeSystem();
			NodeFactory nf = extInfo.nodeFactory();
			ErrorQueue eq = extInfo.compiler().errorQueue();
			polyglot.main.Version v = extInfo.version();*/
	
			//Goal g = internGoal(new VisitorGoal(job, new ClassSerializer(ts, nf, job.source().lastModified(), eq, v))
			Goal g = internGoal(new Serialized(job)
				{
					public Collection prerequisiteGoals(Scheduler scheduler)
					{
						List l = new ArrayList();
		
						l.addAll(super.prerequisiteGoals(scheduler));								
						//l.add(SJNoAliasTranslation(job));
						l.add(SJCompoundOperationTranslation(job));
		
						return l;
					}
				}
			);
		
			return g;
		}
		// End of Serialized phase.			
	}
}
