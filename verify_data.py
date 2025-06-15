import psycopg2
from tabulate import tabulate

db_params = {
    "host": "localhost",
    "database": "testautomationdb",
    "user": "postgres",
    "password": "2649",
    "port": 5432,
}


def check_test_cases():
    try:
        with psycopg2.connect(**db_params) as conn:
            with conn.cursor() as cursor:
                print("\n=== TEST CASES ===")
                cursor.execute(
                    """
                    SELECT 
                        tc.user_story_id,
                        tc.test_case_id,
                        tc.test_objective,
                        tc.pre_condition
                    FROM test_cases tc
                    ORDER BY tc.user_story_id, tc.test_case_id;
                """
                )
                test_cases = cursor.fetchall()
                headers = [
                    "User Story ID",
                    "Test Case ID",
                    "Test Objective",
                    "Pre-Condition",
                ]
                print(
                    tabulate(
                        test_cases,
                        headers=headers,
                        tablefmt="simple",
                        maxcolwidths=[15, 15, 40, 40],
                    )
                )

                print("\n=== TEST STEPS ===")
                cursor.execute(
                    """
                    SELECT 
                        tc.test_case_id || ' (Step ' || COALESCE(ts.step_number::text, 'N/A') || ')',
                        COALESCE(ts.step_description, ''),
                        COALESCE(ts.test_data, ''),
                        COALESCE(ts.expected_result, ''),
                        COALESCE(ts.actual_result, ''),
                        CASE WHEN COALESCE(ts.is_home, false) THEN 'Yes' ELSE 'No' END,
                        tc.test_case_id,
                        ts.step_number
                    FROM test_cases tc
                    LEFT JOIN test_steps ts ON tc.id = ts.test_case_id
                    WHERE ts.step_number IS NOT NULL
                    ORDER BY tc.test_case_id, ts.step_number
                """
                )
                steps = cursor.fetchall()
                if steps:
                    # Remove the last two columns used for sorting
                    formatted_steps = [row[:-2] for row in steps]
                    step_headers = [
                        "Test Case",
                        "Description",
                        "Test Data",
                        "Expected Result",
                        "Actual Result",
                        "Is Home",
                    ]
                    print(
                        tabulate(
                            formatted_steps,
                            headers=step_headers,
                            tablefmt="simple",
                            maxcolwidths=[20, 30, 30, 30, 30, 8],
                        )
                    )
                else:
                    print("No test steps found in the database.")

    except Exception as e:
        print(f"Error: {str(e)}")


if __name__ == "__main__":
    check_test_cases()
