package src_girela_castellon_ruben;

import java.util.*;
import core.game.Observation;
import core.game.StateObservation;
import core.logging.Logger;
import core.player.AbstractPlayer;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Utils;
import tracks.ArcadeMachine;
import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

/**
 * 
 * @author Ruben Girela Castellón
 * Practice 1 (done)
 * - level 0 (simple deliberative agent)
 *
 */

public class myAgent extends AbstractPlayer{
	// 1) Busca la puerta mas cercana. 
	// 2) Escoge la ruta optima a ir a la puerta usando el algoritmo A*.
	Vector2d fescala;
	Vector2d portal;
	
	/**
	 * initialize all variables for the agent
	 * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
	 */
	public myAgent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		//Calculamos el factor de escala entre mundos (pixeles -> grid)
        fescala = new Vector2d(stateObs.getWorldDimension().width / stateObs.getObservationGrid().length , 
        		stateObs.getWorldDimension().height / stateObs.getObservationGrid()[0].length);      
      
        //Se crea una lista de observaciones de portales, ordenada por cercania al avatar
        ArrayList<Observation>[] posiciones = stateObs.getPortalsPositions(stateObs.getAvatarPosition());
        //Seleccionamos el portal mas proximo
        portal = posiciones[0].get(0).position;
        portal.x = Math.floor(portal.x / fescala.x);
        portal.y = Math.floor(portal.y / fescala.y);
        
        //Calcula una ruta optima usando A*
        
	}
	
	/**
	 * Apply the actions
	 * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
	 */
	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		// TODO Auto-generated method stub
		return null;
	}
	/*
	 * Algortimo A* que calcula la ruta optima hacia el portal
	 */
	public void algoritmoEstrella(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		//created the vector of open and closed nodes
		Vector<Vector2d> abiertos = new Vector<Vector2d>();
		Vector<Vector2d> cerrados = new Vector<Vector2d>();
		
		//end of algorithm stop
		boolean stop = false;
		
		//get avater position
		Vector2d avatar =  new Vector2d(stateObs.getAvatarPosition().x / fescala.x, 
        		stateObs.getAvatarPosition().y / fescala.y);
		
		//add the first node (the current position of avatar)
		abiertos.add(avatar);
		
		do {
			
		}while(!stop);
	}
	
	/**
	 * Obtiene el mejor nodo
	 * @param abiertos vector of non-transversed nodes
	 */
	public Vector2d bestNode(Vector<Vector2d> abiertos) {
		Vector2d best_node;
		
		return best_node;
	}
}
