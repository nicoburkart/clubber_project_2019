//
//  TableViewControllerEvents.swift
//  clubber(ios)
//
//  Created by Nico Burkart on 28.04.19.
//  Copyright © 2019 hdm-stuttgart. All rights reserved.
//

import UIKit
import CoreData

class TableViewControllerEvents : UITableViewController{
    
    var eventArr : [Event] = []
    var refreshcontrol : UIRefreshControl?
    @IBOutlet var table: UITableView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        tableView.separatorColor = UIColor(white: 0.95, alpha: 1)
        tableView.dataSource = self
        tableView.delegate = self
        
        refreshcontrol = UIRefreshControl()
        //text displayed under the circle
        refreshcontrol?.attributedTitle = NSAttributedString(string : "Pull to refresh")
        //selector is called when the refreshControl is swiped down
        //ToDo: bei mir kommt hier ein Fehler ??
        refreshcontrol?.addTarget(self, action: #selector(refreshControlPulledDown) , for: .valueChanged)
        
        table.addSubview(refreshcontrol!)
        if !HTTPHelper.requestResponseServerIsRunning{
            eventArr = DataBaseHelper.requestEventsFromDatabase(context: DataBaseHelper.getContext())
        }
        giveUserFeedbackIfNecessary(arr: eventArr)        
    }
    
    //sets the amount of rows the table will have
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return eventArr.count
    }
    
    
    
    //creates a cell and returns it
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> EventCell {
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "reuseIdentEvents", for: indexPath) as! EventCell

        cell.setEventCellValues(event : eventArr[indexPath.row])
        
        cell.contentView.backgroundColor = UIColor(white: 0.95, alpha: 1)
        
        cell.selectionStyle = .none
        
        return cell;
    }
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    
    //Hier muss ein Thread eingefügt werden, der mindestens eine sekunde lang geht, um zu verhindern, dass die refreshfunktion zu schnell beendet und der user denkt, dass nicht refresht worden ist. Dieser Thread sollte in die dispatchGroup eingefügt werden, jedoch NICHT in HTTP helper, da das nicht effizient wäre. Es müsste eine zusätzliche dispatch group nur für die refresh control existieren.
    @objc func refreshControlPulledDown(){
        if(HTTPHelper.hasNetworkAccess && !HTTPHelper.requestResponseServerIsRunning){
            HTTPHelper.requestResponseServer()
            //wait until the thread inside requestResponseServer() has done its job
            HTTPHelper.dispatchGroup.notify(queue: .main){
                UIApplication.shared.isNetworkActivityIndicatorVisible = false
                if (DataBaseHelper.newEventEntriesHaveBeenStored){
                    self.eventArr = DataBaseHelper.requestEventsFromDatabase(context: DataBaseHelper.getContext())
                    NSLog("TableView is about to be updated")
                    self.table.reloadData()
                }
                else {
                    NSLog("TableView does not need to be updated because there is no new data")
                }
                self.refreshcontrol?.endRefreshing()
                }
        }else{
            //needs to be called in order for the refresh process to be stopped
            let alert = UIAlertController(title: "Placholder", message: "Stelle bitte eine Internetverbindung her", preferredStyle: UIAlertController.Style.alert)
        
            //refreshControl will be stoped once user dialogue shows up
            self.present(alert, animated: true, completion: {() -> Void in self.refreshcontrol?.endRefreshing()})
            
            alert.addAction(UIAlertAction(title: "Ok", style: UIAlertAction.Style.default, handler: { (action) in
                alert.dismiss(animated: true, completion: nil)
            }))
        }
        NSLog("Refresh button has been clicked")
    }
}
