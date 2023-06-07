from firebase import firebase

class Firebase:
    def __init__(self, tableName, handPresentGroup, fingersPresentList) -> None: 
        self.tableName = tableName
        self.handPresentGroup = handPresentGroup
        self.fingersPresentList = fingersPresentList

    def insertQuery(self) -> None or str:
        database = firebase.FirebaseApplication("https://pythonandroidstudiocomm-default-rtdb.firebaseio.com/", None)
        dictDatabase = {}
        handPresentGroupSplitted = self.handPresentGroup.split(' ') 
        handPresentGroupSplitted.pop() 
        for index, item in enumerate(handPresentGroupSplitted):
            key = str(index) + ") " + item.split('-')[0]
            value = self.listToString(self.fingersPresentList[index])
            dictDatabase[key] = value
        try:
            database.post(self.tableName, dictDatabase)
        except Exception as e:
            return f"Firebase exception: {e}"

    def listToString(self, _list) -> str:
        return " ".join(_list[:-1])