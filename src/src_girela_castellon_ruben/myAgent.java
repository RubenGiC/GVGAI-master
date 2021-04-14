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
        algoritmoEstrella(stateObs, elapsedTimer);
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
		Vector2d best_node = new Vector2d();
		Integer g = 0;
		
		//end of algorithm stop
		boolean stop = false;
		
		//get avater position
		Vector2d avatar =  new Vector2d(stateObs.getAvatarPosition().x / fescala.x, 
        		stateObs.getAvatarPosition().y / fescala.y);
		
		//add the first node (the current position of avatar)
		abiertos.add(avatar);
		
		do {
			//save the best node
			best_node = bestNode(abiertos, g);
			
			if(best_node == portal) {//if it is the target node 
				//expand the nodes of the actual node
				expandNodes(abiertos,cerrados,best_node,stateObs);
				//and end
				stop = true;
			}else {//else remove the node from the open list
				if(abiertos.indexOf(best_node)>-1)
					abiertos.remove(abiertos.indexOf(best_node));
			}
			stop = true;
		}while(!stop);
	}
	
	/**
	 * Obtiene el mejor nodo
	 * @param abiertos vector of non-transversed nodes
	 */
	public Vector2d bestNode(Vector<Vector2d> abiertos, Integer g) {
		//save the best node
		Vector2d best_node = new Vector2d();
		//save the value h, f and the minimum f
		Integer h=0, f=0, min_f = 9999999;
		
		//traverse all open nodes
		for(Vector2d nodo:abiertos) {
			//calculate heuristic distance
			h = calculateManhattan(nodo,0);
			//and calculate the total distance
			f = g + h;
			//save the node with the smallest distance
			if(f < min_f) {
				f = min_f;
				best_node = nodo;
			}
		}
		//return the best node
		return best_node;
	}
	/**
	 * gets the Manhattan distance
	 * @param current_node current node to calculate distance h 
	 */
	public Integer calculateManhattan(Vector2d current_node, Integer orientacion){
		//Manhattan distance
        Integer distance = 0;
        /*switch(orientacion) {
        case 0://up
        	distance = ((int) (Math.abs(current_node.x - portal.x) + Math.abs(current_node.y-portal.y)));
        	break;
        case 1://down
        	distance = ((int) (Math.abs(current_node.x - portal.x) + Math.abs(current_node.y-portal.y)));
        	break;
        case 2://left
        	distance = ((int) (Math.abs(current_node.x - portal.x) + Math.abs(current_node.y-portal.y)));
        	break;
        case 3://right
        	distance = ((int) (Math.abs(current_node.x - portal.x) + Math.abs(current_node.y-portal.y)));
        }*/
        
        
        //calculate the Manhattan distance 
        distance = ((int) (Math.abs(current_node.x - portal.x) + Math.abs(current_node.y-portal.y))); 
        
        return distance;
        
	}
	
	
	public Vector<Vector2d> expandNodes(Vector<Vector2d> abiertos, Vector<Vector2d> cerrados, Vector2d node, StateObservation stateObs){
		
		//copy unexplored nodes
		Vector <Vector2d> copy_abiertos = abiertos;
		//expand node
		Vector2d new_node;
		ArrayList<Observation> casilla;
		boolean not_wall = false;
		
		//para obtener el tipo de superficie utilizar esto:
		//stateObs.getObservationGrid()[(int)(portal.x)][(int)(portal.y)]);
		
		//[Observation{category=6, itype=9, obsID=99, position=16.0 : 32.0, reference=-1.0 : -1.0, sqDist=1378.0}]
		// esto devuelve un arraylist, pero solo nos interesa category
		
		
		//expand the nodes of the current node
		if (node.y - 1 >= 0) {//abajo
			//save the new node
			new_node = new Vector2d(node.x, node.y-1);
			//save the properties of the box
			casilla = stateObs.getObservationGrid()[(int)(node.x)][(int)(node.y)];
			
			//if not empty
			if(casilla.size()>0) {
				//check if it isn't a wall
				if(casilla.get(0).category != 4)
					not_wall = true;
			}else {
				not_wall = true;
			}
			
			//if the new node hasn't been explored
			if(cerrados.indexOf(new_node) != -1 && not_wall)
				copy_abiertos.add(new_node);//adds it to the list of open nodes
        }
		//and repeat for other nodes (up, right and left)
        if (node.y + 1 <= stateObs.getObservationGrid()[0].length-1) {//arriba
        	
        	new_node = new Vector2d(node.x, node.y+1);
			casilla = stateObs.getObservationGrid()[(int)(node.x)][(int)(node.y)];
			
			if(casilla.size()>0) {
				
				if(casilla.get(0).category != 4)
					not_wall = true;
			}else {
				not_wall = true;
			}
			
			if(cerrados.indexOf(new_node) != -1 && not_wall)
				copy_abiertos.add(new_node);
        }
        if (node.x - 1 >= 0) {//izquierda
        	
        	new_node = new Vector2d(node.x-1, node.y);
			casilla = stateObs.getObservationGrid()[(int)(node.x)][(int)(node.y)];
			
			if(casilla.size()>0) {
				
				if(casilla.get(0).category != 4)
					not_wall = true;
			}else {
				not_wall = true;
			}
			
			if(cerrados.indexOf(new_node) != -1 && not_wall)
				copy_abiertos.add(new_node);
        }
        if (node.x + 1 <= stateObs.getObservationGrid().length - 1) {//derecha
        	
        	new_node = new Vector2d(node.x+1, node.y);
			casilla = stateObs.getObservationGrid()[(int)(node.x)][(int)(node.y)];
			
			if(casilla.size()>0) {
				
				if(casilla.get(0).category != 4)
					not_wall = true;
			}else {
				not_wall = true;
			}
			
			if(cerrados.indexOf(new_node) != -1 && not_wall)
				copy_abiertos.add(new_node);
        }
		
		return copy_abiertos;
	}
}
