package machine

class CoffeeMachine(waterml: Int, milkml: Int, beans: Int, cups: Int, money: Int) {
    private var _waterml = waterml
    private var _milkml = milkml
    private var _beans = beans
    private var _cups = cups
    private var _money = money

    fun printInfo() = println("\nThe coffee machine has:\n" +
            "$_waterml of water\n" +
            "$_milkml of milk\n" +
            "$_beans of coffee beans\n" +
            "$_cups of disposable cups\n" +
            "\$$_money of money")

    private fun checkResources(waterml: Int, milkml: Int, beans: Int): Boolean {
        if (_waterml < waterml) {
            println("Sorry, not enough water!")
            return false
        }
        if (_milkml < milkml) {
            println("Sorry, not enough milk!")
            return false
        }
        if (_beans < beans) {
            println("Sorry, not enough beans!")
            return false
        }
        if (_cups < 1) {
            println("Sorry, not enough cups!")
            return false
        }
        println("I have enough resources, making you a coffee!")
        return true
    }

    private fun buyEspresso() {
        if (!checkResources(250, 0, 16)) return
        _waterml -= 250
        _beans -= 16
        _money += 4
        --_cups
    }

    private fun buyLatte() {
        if (!checkResources(350, 75, 20)) return
        _waterml -= 350
        _milkml -= 75
        _beans -= 20
        _money += 7
        --_cups
    }

    private fun buyCappuccino() {
        if (!checkResources(200, 100, 12)) return
        _waterml -= 200
        _milkml -= 100
        _beans -= 12
        _money += 6
        --_cups
    }

    fun buy() {
        print("\nWhat do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu: ")
        when (readLine()!!) {
            "1" -> buyEspresso()
            "2" -> buyLatte()
            "3" -> buyCappuccino()
            "back" -> return
        }
    }

    fun fill() {
        print("\nWrite how many ml of water do you want to add: ")
        _waterml += readLine()!!.toInt()
        print("Write how many ml of milk do you want to add: ")
        _milkml += readLine()!!.toInt()
        print("Write how many grams of coffee beans do you want to add: ")
        _beans += readLine()!!.toInt()
        print("Write how many disposable cups of coffee do you want to add: ")
        _cups += readLine()!!.toInt()
    }

    fun take() {
        println("\nI gave you \$$_money")
        _money = 0
    }
}

fun main() {
    val cm = CoffeeMachine(400, 540, 120, 9, 550)

    while (true) {
        print("\nWrite action (buy, fill, take, remaining, exit): ")
        when (readLine()!!) {
            "buy" -> cm.buy()
            "fill" -> cm.fill()
            "take" -> cm.take()
            "remaining" -> cm.printInfo()
            "exit" -> break
        }
    }
}
